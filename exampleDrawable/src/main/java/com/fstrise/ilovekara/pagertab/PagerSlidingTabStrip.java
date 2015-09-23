package com.fstrise.ilovekara.pagertab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fstrise.ilovekara.R;

public class PagerSlidingTabStrip extends HorizontalScrollView implements
		View.OnClickListener {
	private int currentPosition; // 当�?�?置
	private int lastOffset;
	private int lastScrollX = 0;
	private float currentPositionOffset; // 当�?�?置�??移�?
	private boolean start;
	private boolean allowWidthFull; // 内容宽度无法充满时，�?许自动调整Item的宽度以充满
	private boolean disableViewPager; // �?用ViewPager
	private View currentSelectedTabView; // 当�?标题项
	private Drawable slidingBlockDrawable; // 滑�?�
	private ViewPager viewPager; // ViewPager
	private ViewGroup tabsLayout; // 标题项布局
	private ViewPager.OnPageChangeListener onPageChangeListener; // 页�?�改�?�监�?�器
	private OnClickTabListener onClickTabListener;
	private List<View> tabViews;
	private boolean disableTensileSlidingBlock; // �?止拉伸滑�?�图片
	private TabViewFactory tabViewFactory;

	public PagerSlidingTabStrip(Context context) {
		this(context, null);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHorizontalScrollBarEnabled(false); // �?�?横�?�滑动�??示�?�
		removeAllViews();
		if (attrs != null) {
			TypedArray attrsTypedArray = context.obtainStyledAttributes(attrs,
					R.styleable.PagerSlidingTabStrip2);
			if (attrsTypedArray != null) {
				allowWidthFull = attrsTypedArray
						.getBoolean(
								R.styleable.PagerSlidingTabStrip2_allowWidthFull,
								false);
				slidingBlockDrawable = attrsTypedArray
						.getDrawable(R.styleable.PagerSlidingTabStrip2_slidingBlock);
				disableViewPager = attrsTypedArray.getBoolean(
						R.styleable.PagerSlidingTabStrip2_disableViewPager,
						false);
				disableTensileSlidingBlock = attrsTypedArray
						.getBoolean(
								R.styleable.PagerSlidingTabStrip2_disableTensileSlidingBlock,
								false);
				attrsTypedArray.recycle();
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (allowWidthFull && tabsLayout != null) {
			View childView;
			for (int w = 0, size = tabsLayout.getChildCount(); w < size; w++) {
				childView = tabsLayout.getChildAt(w);
				ViewGroup.LayoutParams params = childView.getLayoutParams();
				params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
				childView.setLayoutParams(params);
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!allowWidthFull) {
			return;
		}
		ViewGroup tabsLayout = getTabsLayout();
		if (tabsLayout == null) {
			return;
		}
		if (tabsLayout.getChildCount() <= 0) {
			return;
		}

		if (tabViews == null) {
			tabViews = new ArrayList<View>();
		} else {
			tabViews.clear();
		}
		for (int w = 0; w < tabsLayout.getChildCount(); w++) {
			tabViews.add(tabsLayout.getChildAt(w));
		}

		adjustChildWidthWithParent(
				tabViews,
				getMeasuredWidth() - tabsLayout.getPaddingLeft()
						- tabsLayout.getPaddingRight(), widthMeasureSpec,
				heightMeasureSpec);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	
	private void adjustChildWidthWithParent(List<View> views,
			int parentViewWidth, int parentWidthMeasureSpec,
			int parentHeightMeasureSpec) {
		for (View view : views) {
			if (view.getLayoutParams() instanceof MarginLayoutParams) {
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view
						.getLayoutParams();
				parentViewWidth -= lp.leftMargin + lp.rightMargin;
			}
		}
		int averageWidth = parentViewWidth / views.size();
		int bigTabCount = views.size();
		while (true) {
			Iterator<View> iterator = views.iterator();
			while (iterator.hasNext()) {
				View view = iterator.next();
				if (view.getMeasuredWidth() > averageWidth) {
					parentViewWidth -= view.getMeasuredWidth();
					bigTabCount--;
					iterator.remove();
				}
			}
			if (bigTabCount <= 0) {
				break;
			}
			averageWidth = parentViewWidth / bigTabCount;
			boolean end = true;
			for (View view : views) {
				if (view.getMeasuredWidth() > averageWidth) {
					end = false;
				}
			}
			if (end) {
				break;
			}
		}

		for (View view : views) {
			if (view.getMeasuredWidth() < averageWidth) {
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view
						.getLayoutParams();
				layoutParams.width = averageWidth;
				view.setLayoutParams(layoutParams);
				if (layoutParams instanceof MarginLayoutParams) {
					measureChildWithMargins(view, parentWidthMeasureSpec, 0,
							parentHeightMeasureSpec, 0);
				} else {
					measureChild(view, parentWidthMeasureSpec,
							parentHeightMeasureSpec);
				}
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		ViewGroup tabViewGroup = getTabsLayout();
		if (tabViewGroup != null) {
			currentPosition = viewPager != null ? viewPager.getCurrentItem()
					: 0;
			if (!disableViewPager) {
				scrollToChild(currentPosition, 0); // 移动滑�?�到指定�?置
				selectedTab(currentPosition); // 选中指定�?置的TAB
			}

			for (int w = 0; w < tabViewGroup.getChildCount(); w++) {
				View itemView = tabViewGroup.getChildAt(w);
				itemView.setTag(w);
				itemView.setOnClickListener(this);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int index = (Integer) v.getTag();
		if (onClickTabListener != null) {
			onClickTabListener.onClickTab(v, index);
		}
		if (viewPager != null) {
			viewPager.setCurrentItem(index, true);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (disableViewPager)
			return;
		ViewGroup tabsLayout = getTabsLayout();
		if (tabsLayout != null && tabsLayout.getChildCount() > 0
				&& slidingBlockDrawable != null) {
			View currentTab = tabsLayout.getChildAt(currentPosition);
			if (currentTab != null) {
				float slidingBlockLeft = currentTab.getLeft();
				float slidingBlockRight = currentTab.getRight();
				if (currentPositionOffset > 0f
						&& currentPosition < tabsLayout.getChildCount() - 1) {
					View nextTab = tabsLayout.getChildAt(currentPosition + 1);
					if (nextTab != null) {
						final float nextTabLeft = nextTab.getLeft();
						final float nextTabRight = nextTab.getRight();
						slidingBlockLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset)
								* slidingBlockLeft);
						slidingBlockRight = (currentPositionOffset
								* nextTabRight + (1f - currentPositionOffset)
								* slidingBlockRight);
					}
				}

				// �?拉伸
				if (disableTensileSlidingBlock) {
					int center = (int) (slidingBlockLeft + (slidingBlockRight - slidingBlockLeft) / 2);
					slidingBlockLeft = center
							- slidingBlockDrawable.getIntrinsicWidth() / 2;
					slidingBlockRight = center
							+ slidingBlockDrawable.getIntrinsicWidth() / 2;
				}

				slidingBlockDrawable
						.setBounds((int) slidingBlockLeft, getHeight()
								- slidingBlockDrawable.getIntrinsicHeight(),
								(int) slidingBlockRight, getHeight());
				slidingBlockDrawable.draw(canvas);
			}
		}
	}

	/**
	 * 获�?�布局
	 */
	private ViewGroup getTabsLayout() {
		if (tabsLayout == null) {
			if (getChildCount() > 0) {
				tabsLayout = (ViewGroup) getChildAt(0);
			} else {
				removeAllViews();
				LinearLayout tabsLayout = new LinearLayout(getContext());
				tabsLayout.setGravity(Gravity.CENTER_VERTICAL);
				this.tabsLayout = tabsLayout;
				addView(tabsLayout, new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
			}
		}
		return tabsLayout;
	}

	/**
	 * �?置，清除所有tab
	 */
	public void reset() {
		if (tabViewFactory != null) {
			tabViewFactory.addTabs(getTabsLayout(),
					viewPager != null ? viewPager.getCurrentItem() : 0);
		}
	}

	/**
	 * 获�?�Tab
	 * 
	 * @param position
	 *            �?置
	 * @return Tab的View
	 */
	public View getTab(int position) {
		if (tabsLayout != null && tabsLayout.getChildCount() > position) {
			return tabsLayout.getChildAt(position);
		} else {
			return null;
		}
	}

	/**
	 * 滚动到指定的�?置
	 */
	private void scrollToChild(int position, int offset) {
		ViewGroup tabsLayout = getTabsLayout();
		if (tabsLayout != null && tabsLayout.getChildCount() > 0
				&& position < tabsLayout.getChildCount()) {
			View view = tabsLayout.getChildAt(position);
			if (view != null) {
				// 计算新的X�??标
				int newScrollX = view.getLeft() + offset - getLeftMargin(view);
				if (position > 0 || offset > 0) {
					newScrollX -= getWidth() / 2 - getOffset(view.getWidth())
							/ 2;
				}

				// 如果�?�上次X�??标�?一样就执行滚动
				if (newScrollX != lastScrollX) {
					lastScrollX = newScrollX;
					scrollTo(newScrollX, 0);
				}
			}
		}
	}

	private int getLeftMargin(View view) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		if (params instanceof MarginLayoutParams) {
			MarginLayoutParams marginParams = (MarginLayoutParams) params;
			return marginParams.leftMargin;
		}
		return 0;
	}

	private int getRightMargin(View view) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		if (params instanceof MarginLayoutParams) {
			MarginLayoutParams marginParams = (MarginLayoutParams) params;
			return marginParams.rightMargin;
		}
		return 0;
	}

	/**
	 * 获�?��??移�?
	 */
	private int getOffset(int newOffset) {
		if (lastOffset < newOffset) {
			if (start) {
				lastOffset += 1;
				return lastOffset;
			} else {
				start = true;
				lastOffset += 1;
				return lastOffset;
			}
		}
		if (lastOffset > newOffset) {
			if (start) {
				lastOffset -= 1;
				return lastOffset;
			} else {
				start = true;
				lastOffset -= 1;
				return lastOffset;
			}
		} else {
			start = true;
			lastOffset = newOffset;
			return lastOffset;
		}
	}

	/**
	 * 选中指定�?置的TAB
	 */
	private void selectedTab(int currentSelectedTabPosition) {
		ViewGroup tabsLayout = getTabsLayout();
		if (currentSelectedTabPosition > -1 && tabsLayout != null
				&& currentSelectedTabPosition < tabsLayout.getChildCount()) {
			if (currentSelectedTabView != null) {
				currentSelectedTabView.setSelected(false);
			}
			currentSelectedTabView = tabsLayout
					.getChildAt(currentSelectedTabPosition);
			if (currentSelectedTabView != null) {
				currentSelectedTabView.setSelected(true);
			}
		}
	}

	/**
	 * 设置ViewPager
	 * 
	 * @param viewPager
	 *            ViewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		if (disableViewPager)
			return;
		this.viewPager = viewPager;
		this.viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				selectedTab(position);
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageSelected(position);
				}
			}

			@Override
			public void onPageScrolled(int nextPagePosition,
					float positionOffset, int positionOffsetPixels) {
				ViewGroup tabsLayout = getTabsLayout();
				if (nextPagePosition < tabsLayout.getChildCount()) {
					View view = tabsLayout.getChildAt(nextPagePosition);
					if (view != null) {
						currentPosition = nextPagePosition;
						currentPositionOffset = positionOffset;
						scrollToChild(
								nextPagePosition,
								(int) (positionOffset * (view.getWidth()
										+ getLeftMargin(view) + getRightMargin(view))));
						invalidate();
					}
				}
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrolled(nextPagePosition,
							positionOffset, positionOffsetPixels);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrollStateChanged(arg0);
				}
			}
		});
		requestLayout();
	}

	/**
	 * 设置Page切�?�监�?�器
	 * 
	 * @param onPageChangeListener
	 *            Page切�?�监�?�器
	 */
	public void setOnPageChangeListener(
			OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}

	/**
	 * 设置是�?�充满�?幕
	 * 
	 * @param allowWidthFull
	 *            true：当内容的宽度无法充满�?幕时，自动调整�
	 *            �?一个Item的宽度以充满�?幕
	 */
	public void setAllowWidthFull(boolean allowWidthFull) {
		this.allowWidthFull = allowWidthFull;
		requestLayout();
	}

	/**
	 * 设置滑�?�图片
	 */
	public void setSlidingBlockDrawable(Drawable slidingBlockDrawable) {
		this.slidingBlockDrawable = slidingBlockDrawable;
		requestLayout();
	}

	/**
	 * 设置是�?��?止拉伸滑�?�图片
	 * 
	 * @param disableTensileSlidingBlock
	 *            是�?��?止拉伸滑�?�图片
	 */
	public void setDisableTensileSlidingBlock(boolean disableTensileSlidingBlock) {
		this.disableTensileSlidingBlock = disableTensileSlidingBlock;
		invalidate();
	}

	/**
	 * 获�?�Tab总数
	 */
	public int getTabCount() {
		ViewGroup tabsLayout = getTabsLayout();
		return tabsLayout != null ? tabsLayout.getChildCount() : 0;
	}

	/**
	 * 设置Tab点击监�?�器
	 * 
	 * @param onClickTabListener
	 *            Tab点击监�?�器
	 */
	public void setOnClickTabListener(OnClickTabListener onClickTabListener) {
		this.onClickTabListener = onClickTabListener;
	}

	/**
	 * 设置�?使用ViewPager
	 * 
	 * @param disableViewPager
	 *            �?使用ViewPager
	 */
	public void setDisableViewPager(boolean disableViewPager) {
		this.disableViewPager = disableViewPager;
		if (viewPager != null) {
			viewPager.setOnPageChangeListener(onPageChangeListener);
			viewPager = null;
		}
		requestLayout();
	}

	/**
	 * 设置TabView生�?器
	 * 
	 * @param tabViewFactory
	 */
	public void setTabViewFactory(TabViewFactory tabViewFactory) {
		this.tabViewFactory = tabViewFactory;
		tabViewFactory.addTabs(getTabsLayout(),
				viewPager != null ? viewPager.getCurrentItem() : 0);
	}

	/**
	 * Tab点击监�?�器
	 */
	public interface OnClickTabListener {
		public void onClickTab(View tab, int index);
	}

	/**
	 * TabView生�?器
	 */
	public interface TabViewFactory {
		/**
		 * 添加tab
		 * 
		 * @param parent
		 * @param defaultPosition
		 */
		public void addTabs(ViewGroup parent, int defaultPosition);
	}
}