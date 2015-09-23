package com.fstrise.ilovekara.customdialog;

import com.fstrise.ilovekara.customdialog.effect.BaseEffects;
import com.fstrise.ilovekara.customdialog.effect.FadeIn;
import com.fstrise.ilovekara.customdialog.effect.Fall;
import com.fstrise.ilovekara.customdialog.effect.FlipH;
import com.fstrise.ilovekara.customdialog.effect.FlipV;
import com.fstrise.ilovekara.customdialog.effect.NewsPaper;
import com.fstrise.ilovekara.customdialog.effect.RotateBottom;
import com.fstrise.ilovekara.customdialog.effect.RotateLeft;
import com.fstrise.ilovekara.customdialog.effect.Shake;
import com.fstrise.ilovekara.customdialog.effect.SideFall;
import com.fstrise.ilovekara.customdialog.effect.SlideBottom;
import com.fstrise.ilovekara.customdialog.effect.SlideLeft;
import com.fstrise.ilovekara.customdialog.effect.SlideRight;
import com.fstrise.ilovekara.customdialog.effect.SlideTop;
import com.fstrise.ilovekara.customdialog.effect.Slit;

/**
 * Created by lee on 2014/7/30.
 */
public enum Effectstype {

	Fadein(FadeIn.class), Slideleft(SlideLeft.class), Slidetop(SlideTop.class), SlideBottom(
			SlideBottom.class), Slideright(SlideRight.class), Fall(Fall.class), Newspager(
			NewsPaper.class), Fliph(FlipH.class), Flipv(FlipV.class), RotateBottom(
			RotateBottom.class), RotateLeft(RotateLeft.class), Slit(Slit.class), Shake(
			Shake.class), Sidefill(SideFall.class);
	private Class<? extends BaseEffects> effectsClazz;

	private Effectstype(Class<? extends BaseEffects> mclass) {
		effectsClazz = mclass;
	}

	public BaseEffects getAnimator() {
		BaseEffects bEffects = null;
		try {
			bEffects = effectsClazz.newInstance();
		} catch (ClassCastException e) {
			throw new Error("Can not init animatorClazz instance");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new Error("Can not init animatorClazz instance");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new Error("Can not init animatorClazz instance");
		}
		return bEffects;
	}
}
