package fr.delicatessences.delicatessences.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.LoginActivity;
import fr.delicatessences.delicatessences.adapters.NavigationDrawerArrayAdapter;
import fr.delicatessences.delicatessences.model.persistence.SynchronizationHelper;
import fr.delicatessences.delicatessences.utils.ProgressModalDialogHolder;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements ResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int NB_MENU_ITEMS= 8;

    private static final int NB_ACCOUNT_ITEMS= 2;

    private static final int RC_READ = 18;

    private static final String PASSWORD_PROVIDER = "password";

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mMainMenuList;
	private ListView mAccountList;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 1;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;
    private ImageView mArrowView;
    private ProgressModalDialogHolder mProgressHolder;
    private GoogleApiClient mSigninApiClient;


    public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        FragmentActivity activity = getActivity();




		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(activity);
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

        mProgressHolder = new ProgressModalDialogHolder(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
        if (savedInstanceState == null){
            selectItem(mCurrentSelectedPosition);
        }

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mMainMenuList = (ListView) view.findViewById(R.id.list);
		mMainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});



        mAccountList = (ListView) view.findViewById(R.id.account_list);
        mAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 1){
                            showConfirmDeleteDialog(activity);
                        } else if (position == 2){
                            logout();
                        }
                    }
                });

        addHeader(inflater, container);
        populateList(activity);
        populateAccountList(activity);

		return view;
	}


    private void logout(){
        //logout
        final FragmentActivity activity = getActivity();
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                    }
                });
    }

    private void showConfirmDeleteDialog(final FragmentActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(getResources().getString(R.string.delete_account_message));
        final Resources resources = getResources();
        builder.setPositiveButton(resources.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                        if (currentUser != null){
                            List<String> providerId = currentUser.getProviders();
                            String provider;
                            if (providerId.size() == 1){
                                provider = providerId.get(0);
                            } else {
                                provider = PASSWORD_PROVIDER;
                            }

                            if (provider.equals(PASSWORD_PROVIDER)){
                                showPasswordPromptDialog(activity);
                            } else {
                                GoogleSignInOptions gso =
                                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestEmail()
                                                .requestIdToken(getString(R.string.default_web_client_id))
                                                .build();

                                mSigninApiClient = new GoogleApiClient.Builder(getActivity())
                                        .enableAutoManage(getActivity(), NavigationDrawerFragment.this)
                                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                        .build();
                                OptionalPendingResult<GoogleSignInResult> opr =
                                        Auth.GoogleSignInApi.silentSignIn(mSigninApiClient);
                                opr.setResultCallback(NavigationDrawerFragment.this);
                            }

                        } else {
                            Toast toast = Toast.makeText(activity, R.string.unknown_error, Toast.LENGTH_LONG);
                            toast.show();
                        }

                    }
                }
        );
        builder.setNegativeButton(resources.getString(R.string.action_cancel), null);
        builder.show();
    }


    private void showPasswordPromptDialog(final FragmentActivity activity){
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.passwordInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String password = userInput.getText().toString();
                                FirebaseAuth instance = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = instance.getCurrentUser();
                                deleteAccount(getMailCredential(currentUser, password));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


	private void deleteAccount(AuthCredential credential){
        final FragmentActivity activity = getActivity();
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null & isConnected){
            final String userId = currentUser.getUid();
            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(NavigationDrawerFragment.class.getName(), "User re-authenticated.");
                            mProgressHolder.showLoadingDialog(R.string.account_deletion);
                            SynchronizationHelper.cancelUploadJob(activity);
                            SynchronizationHelper.deleteRemoteDatabase(userId)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            AuthUI.getInstance()
                                                    .delete(activity)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            SynchronizationHelper.deleteLocalDatabase(userId);
                                                            mProgressHolder.dismissDialog();
                                                            startActivity(new Intent(activity, LoginActivity.class));
                                                            activity.finish();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mProgressHolder.dismissDialog();
                                                    Toast toast = Toast.makeText(activity, R.string.unknown_error, Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            });
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(activity, R.string.error_incorrect_password, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
        } else {
            Toast toast = Toast.makeText(activity, R.string.no_internet_connection, Toast.LENGTH_LONG);
            toast.show();
        }

    }


	private void addHeader(LayoutInflater inflater, ViewGroup container){
        LinearLayout listHeaderView = (LinearLayout)inflater.inflate(
                R.layout.navigation_drawer_header, container, false);
        FirebaseAuth.getInstance();
        View userButton = listHeaderView.findViewById(R.id.user_button);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null){
            TextView usernameTextView = listHeaderView.findViewById(R.id.username);
            usernameTextView.setText(currentUser.getDisplayName());
            TextView usermailTextView = listHeaderView.findViewById(R.id.usermail);
            usermailTextView.setText(currentUser.getEmail());
            mArrowView = listHeaderView.findViewById(R.id.arrow);

            userButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mArrowView.setRotation(mArrowView.getRotation() + 180);
                    if (mAccountList.getVisibility() == View.GONE){
                        mMainMenuList.setVisibility(View.GONE);
                        mAccountList.setVisibility(View.VISIBLE);
                    } else {
                        mAccountList.setVisibility(View.GONE);
                        mMainMenuList.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            userButton.setVisibility(View.GONE);
        }
        mAccountList.addHeaderView(listHeaderView, null, false);
        mMainMenuList.addHeaderView(listHeaderView, null, false);
    }

	private void populateList(Context context) {
        Resources resources = getResources();
        TypedArray images = resources.obtainTypedArray(R.array.navigation_drawer_images_white);
        String[] menuItems = resources.getStringArray(R.array.drawer_items);
        List<NavigationDrawerArrayAdapter.NavigationRowItem> items = new ArrayList<>();
        for (int i = 0; i < NB_MENU_ITEMS; i++){
            String title = menuItems[i];
            int imageId = images.getResourceId(i, -1);
            NavigationDrawerArrayAdapter.NavigationRowItem item =
                    new NavigationDrawerArrayAdapter.NavigationRowItem(title, imageId);
            items.add(item);
        }
        images.recycle();

        NavigationDrawerArrayAdapter adapter = new NavigationDrawerArrayAdapter(context, items);
        mMainMenuList.setAdapter(adapter);
        mMainMenuList.setItemChecked(mCurrentSelectedPosition, true);

    }


    private void populateAccountList(Context context) {
        Resources resources = getResources();
        TypedArray images = resources.obtainTypedArray(R.array.navigation_drawer_account_images);
        String[] menuItems = resources.getStringArray(R.array.drawer_account_items);
        List<NavigationDrawerArrayAdapter.NavigationRowItem> items = new ArrayList<>();
        int nbMenuItems = NB_ACCOUNT_ITEMS;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            for (int i = 0; i < nbMenuItems; i++) {
                String title = menuItems[i];
                int imageId = images.getResourceId(i, -1);
                NavigationDrawerArrayAdapter.NavigationRowItem item =
                        new NavigationDrawerArrayAdapter.NavigationRowItem(title, imageId);
                items.add(item);
            }
            images.recycle();

            NavigationDrawerArrayAdapter adapter = new NavigationDrawerArrayAdapter(context, items);
            mAccountList.setAdapter(adapter);
        }
    }


	private boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mMainMenuList != null){
                    if (mMainMenuList.getVisibility() == View.GONE){
                        mArrowView.setRotation(mArrowView.getRotation() + 180);
                    }
                    mMainMenuList.setVisibility(View.VISIBLE);
                }
                if (mAccountList != null){
                    mAccountList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
 /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.apply();
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position) {

		if (mMainMenuList != null) {
			mMainMenuList.setItemChecked(position, true);

		}
		position--;
		mCurrentSelectedPosition = position;
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;



	}


    @Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.empty, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);


	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        return activity.getSupportActionBar();
	}

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onResult(@NonNull Result result) {
        Status status = result.getStatus();
        if (result instanceof GoogleSignInResult){
            if (mSigninApiClient != null){
                mSigninApiClient.disconnect();
                mSigninApiClient.stopAutoManage(getActivity());
            }
            if (status.isSuccess()){
                GoogleSignInResult gsResult = (GoogleSignInResult) result;
                String idToken = gsResult.getSignInAccount().getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                deleteAccount(credential);
            } else {
                Toast toast = Toast.makeText(getActivity(), R.string.unknown_error, Toast.LENGTH_LONG);
            }

        }

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);

	}
	
	
	public void setDrawerIndicatorEnabled(boolean b){
		mDrawerToggle.setDrawerIndicatorEnabled(b);
	}

	private AuthCredential getMailCredential(FirebaseUser user, String password){
        if (password.isEmpty()){
            password ="1234";
        }

        return EmailAuthProvider.getCredential(user.getEmail(), password);

    }

    @Override
    public void onDestroy() {
        mProgressHolder.dismissDialog();
        super.onDestroy();
    }


}
