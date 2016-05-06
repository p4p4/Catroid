/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.utils;

import android.graphics.PointF;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import java.util.List;
import java.util.TreeMap;

public final class TouchUtil {

	private static TouchUtil instance;

	private TreeMap<Integer, PointF> currentlyTouchedFinger;
	private int lastTouchIndex;

	private TouchUtil() {
		currentlyTouchedFinger = new TreeMap<>();
		lastTouchIndex = 0;
	}

	private static TouchUtil getInstance() {
		if (instance == null) {
			instance = new TouchUtil();
		}
		return instance;
	}

	public static void updatePosition(float x, float y, int pointer) {
		getInstance().currentlyTouchedFinger.put(pointer + 1, new PointF(x, y));
	}

	public static void touchDown(float x, float y, int pointer) {
		getInstance().lastTouchIndex = pointer + 1;
		getInstance().currentlyTouchedFinger.put(pointer + 1, new PointF(x, y));
		getInstance().fireTouchEvent();
	}

	public static void touchUp(int pointer) {
		getInstance().currentlyTouchedFinger.remove(pointer + 1);
	}

	public static boolean isFingerTouching(int pointer) {
		return getInstance().currentlyTouchedFinger.containsKey(pointer);
	}

	public static int getLastTouchIndex() {
		return getInstance().lastTouchIndex;
	}

	public static float getX(int pointer) {
		if (isFingerTouching(pointer)) {
			return getInstance().currentlyTouchedFinger.get(pointer).x;
		}

		return 0.0f;
	}

	public static float getY(int pointer) {
		if (isFingerTouching(pointer)) {
			return getInstance().currentlyTouchedFinger.get(pointer).y;
		}

		return 0.0f;
	}

	private void fireTouchEvent() {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();

		for (Sprite sprite : spriteList) {
			sprite.createTouchDownAction();
		}
	}
}
