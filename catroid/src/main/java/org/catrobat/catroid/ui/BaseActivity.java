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
package org.catrobat.catroid.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.AboutDialogFragment;
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.Locale;

public abstract class BaseActivity extends Activity {

	private boolean returnToProjectsList;
	private String titleActionBar;
	private Menu baseMenu;
	private boolean returnByPressingBackButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titleActionBar = null;
		returnToProjectsList = false;
		returnByPressingBackButton = false;
		Thread.setDefaultUncaughtExceptionHandler(new BaseExceptionHandler(this));
		Utils.checkIfCrashRecoveryAndFinishActivity(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onDestroy() {
		// Partly from http://stackoverflow.com/a/5069354
		unbindDrawables(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
		System.gc();

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (getTitleActionBar() != null) {
			getActionBar().setTitle(getTitleActionBar());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		baseMenu = menu;
		getMenuInflater().inflate(R.menu.menu_base_menu, menu);

		final MenuItem scratchConverterMenuItem = menu.findItem(R.id.menu_scratch_converter);
		if (scratchConverterMenuItem != null) {
			if (!(this instanceof MainMenuActivity)) {
				scratchConverterMenuItem.setVisible(false);
			} else {
				final String title = getString(R.string.main_menu_scratch_converter);
				final String beta = getString(R.string.beta).toUpperCase(Locale.getDefault());
				final SpannableString spanTitle = new SpannableString(title + " " + beta);
				final int begin = title.length() + 1;
				final int end = begin + beta.length();
				final int betaLabelColor = ContextCompat.getColor(this, R.color.beta_label_color);
				spanTitle.setSpan(new ForegroundColorSpan(betaLabelColor), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				scratchConverterMenuItem.setTitle(spanTitle);
			}
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (returnToProjectsList) {
					Intent intent = new Intent(this, MyProjectsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else if (returnByPressingBackButton) {
					onBackPressed();
				} else {
					return false;
				}
				break;
			case R.id.settings:
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivity(settingsIntent);
				break;
			case R.id.menu_scratch_converter:
				if (Utils.isNetworkAvailable(this)) {
					final Intent scratchConverterIntent = new Intent(this, ScratchConverterActivity.class);
					startActivity(scratchConverterIntent);
				} else {
					ToastUtil.showError(this, R.string.error_internet_connection);
				}
				break;
			case R.id.menu_rate_app:
				launchMarket();
				return true;
			case R.id.menu_terms_of_use:
				TermsOfUseDialogFragment termsOfUseDialog = new TermsOfUseDialogFragment();
				termsOfUseDialog.show(getFragmentManager(), TermsOfUseDialogFragment.DIALOG_FRAGMENT_TAG);
				return true;
			case R.id.menu_about:
				AboutDialogFragment aboutDialog = new AboutDialogFragment();
				aboutDialog.show(getFragmentManager(), AboutDialogFragment.DIALOG_FRAGMENT_TAG);
				return true;
			case R.id.menu_logout:
				Utils.logoutUser(this);
				return true;
			case R.id.menu_login:
				ProjectManager.getInstance().showSignInDialog(this, false);
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// Taken from http://stackoverflow.com/a/11270668
	private void launchMarket() {
		if (!Utils.isNetworkAvailable(this, true)) {
			return;
		}

		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(myAppLinkToMarket);
		} catch (ActivityNotFoundException e) {
			ToastUtil.showError(this, R.string.main_menu_play_store_not_installed);
		}
	}

	// Code from Stackoverflow to reduce memory problems
	// onDestroy() and unbindDrawables() methods taken from
	// http://stackoverflow.com/a/6779067
	protected void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	public boolean isReturnToProjectsList() {
		return returnToProjectsList;
	}

	public void setReturnToProjectsList(boolean returnToProjectsList) {
		this.returnToProjectsList = returnToProjectsList;
	}

	public void setReturnByPressingBackButton(boolean returnByPressingBackButton) {
		this.returnByPressingBackButton = returnByPressingBackButton;
	}

	public String getTitleActionBar() {
		return titleActionBar;
	}

	public void setTitleActionBar(String titleActionBar) {
		this.titleActionBar = titleActionBar;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem logout = baseMenu.findItem(R.id.menu_logout);
		MenuItem login = baseMenu.findItem(R.id.menu_login);
		logout.setVisible(Utils.isUserLoggedIn(this));
		login.setVisible(!Utils.isUserLoggedIn(this));

		if (!BuildConfig.FEATURE_SCRATCH_CONVERTER_ENABLED) {
			baseMenu.removeItem(R.id.menu_scratch_converter);
		}
		return true;
	}
}
