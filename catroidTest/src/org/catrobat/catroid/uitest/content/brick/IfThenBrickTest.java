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
package org.catrobat.catroid.uitest.content.brick;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class IfThenBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = IfThenBrickTest.class.getSimpleName();
	private Project project;
	private IfThenLogicBeginBrick ifBrick;

	public IfThenBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testIfBrick() {
		ListView view = UiTestUtils.getScriptListView(solo);
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.brick_if_then_begin_edit_text, 5, Brick.BrickField.IF_THEN_CONDITION, ifBrick);

		assertEquals("Incorrect number of bricks.", 5, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 0, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());

		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfThenLogicBeginBrick);
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_if_begin)));
	}

	public void testStrings() {
		solo.clickOnView(solo.getView(R.id.brick_if_then_begin_edit_text));
		solo.sleep(100);

		boolean isFound = solo.searchText(solo.getString(R.string.brick_if_then_begin_second_part));
		assertTrue("String: " + getActivity().getString(R.string.brick_if_then_begin_second_part) + " not found!",
				isFound);

		isFound = solo.searchText(solo.getString(R.string.brick_if_begin));
		assertTrue("String: " + getActivity().getString(R.string.brick_if_begin) + " not found!", isFound);
	}

	public void testCopyIfLogicBeginBrickActionMode() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(1);
		UiTestUtils.acceptAndCloseActionMode(solo);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();

		assertEquals("Incorrect number of bricks.", 5, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfThenLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfThenLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfThenLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfThenLogicEndBrick);
	}

	public void testCopyIfLogicEndBrickActionMode() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(3);
		UiTestUtils.acceptAndCloseActionMode(solo);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 5, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfThenLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfThenLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfThenLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfThenLogicEndBrick);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		ifBrick = new IfThenLogicBeginBrick(0);
		IfThenLogicEndBrick ifEndBrick = new IfThenLogicEndBrick(ifBrick);
		ifBrick.setIfThenEndBrick(ifEndBrick);

		script.addBrick(ifBrick);
		script.addBrick(new ChangeYByNBrick(-10));
		script.addBrick(ifEndBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript());
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
