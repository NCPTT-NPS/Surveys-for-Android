package org.wordpress.android.ui.posts;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.style.AlignmentSpan;
import android.text.style.CharacterStyle;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.xmlrpc.android.ApiHelper;

import org.wordpress.android.Constants;
import org.surveys.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.models.Blog;
import org.wordpress.android.models.MediaFile;
import org.wordpress.android.models.Post;
import org.wordpress.android.ui.accounts.NewAccountActivity;
import org.wordpress.android.util.DeviceUtils;
import org.wordpress.android.util.EscapeUtils;
import org.wordpress.android.util.ImageHelper;
import org.wordpress.android.util.LocationHelper;
import org.wordpress.android.util.LocationHelper.LocationResult;
import org.wordpress.android.util.PostUploadService;
import org.wordpress.android.util.StringHelper;
import org.wordpress.android.util.WPEditText;
import org.wordpress.android.util.WPHtml;
import org.wordpress.android.util.WPImageSpan;
import org.wordpress.android.util.WPUnderlineSpan;

public class EditPostActivity extends SherlockActivity implements OnClickListener, OnTouchListener, TextWatcher,
        WPEditText.OnSelectionChangedListener, OnFocusChangeListener, WPEditText.EditTextImeBackListener {

    private static final int AUTOSAVE_DELAY_MILLIS = 60000;

    private static final int ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY = 0;
    private static final int ACTIVITY_REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int ACTIVITY_REQUEST_CODE_VIDEO_LIBRARY = 2;
    private static final int ACTIVITY_REQUEST_CODE_TAKE_VIDEO = 3;
    private static final int ACTIVITY_REQUEST_CODE_CREATE_LINK = 4;
    private static final int ACTIVITY_REQUEST_CODE_SELECT_CATEGORIES = 5;

    private static final int ID_DIALOG_DATE = 0;
    private static final int ID_DIALOG_TIME = 1;
    private static final int ID_DIALOG_LOADING = 2;

    private Blog mBlog;
    private Post mPost;

    private WPEditText mContentEditText;
    private ImageButton mAddPictureButton;
    private Spinner mStatusSpinner;
    private EditText mTitleEditText, mPasswordEditText, mTagsEditText;
    private TextView mLocationText, mCategoriesText, mPubDateText;
    private ToggleButton mBoldToggleButton, mEmToggleButton, mBquoteToggleButton;
    private ToggleButton mUnderlineToggleButton, mStrikeToggleButton;
    private Button mPubDateButton, mLinkButton, mMoreButton;
    private RelativeLayout mFormatBar;
    
    ////added Jorge Rodriguez
    private ToggleButton mRBCA_bldg_occucy_avail, mRBCA_bldg_outbldg, mRBCA_hist_appear;
    private Spinner mRBCA_bldg_occucy_spinner, mRBCA_coord_loc_spinner, mRBCA_coord_corner_spinner, mRBCA_hist_dist_spinner;
    private Spinner mRBCA_hist_age_spinner, mRBCA_hist_age_meta_spinner,mRBCA_dmg_total_spinner,mRBCA_struct_type_spinner,mRBCA_struct_spinner;
    private Spinner mRBCA_found_type_spinner, mRBCA_found_spinner,mRBCA_extwall_spinner, mRBCA_extfeat_spinner, mRBCA_win_spinner;
    private Spinner mRBCA_roof_spinner,mRBCA_int_collect_extant_spinner,mRBCA_landveg_spinner,mRBCA_landblt_spinner, mRBCA_hist_age_src_spinner;
    private Spinner mRBCA_hzrd_spinner;
    private EditText mRBCA_coord_notes, mRBCA_addr_no, mRBCA_addr_street, mRBCA_coord_loc_oth, mRBCA_bldg_posting_oth;
    private EditText mRBCA_bldg_stories, mRBCA_bldg_width, mRBCA_bldg_length, mRBCA_bldg_use_oth,mRBCA_bldg_outbldg_notes, mRBCA_bldg_units_res,mRBCA_bldg_units_comm;
    private EditText mRBCA_bldg_occu_name, mRBCA_bldg_occu_phone, mRBCA_bldg_notes,mRBCA_hist_desig_oth,mRBCA_hist_dist_name;
    private EditText mRBCA_hist_yr_built,  mRBCA_dmg_source_oth, mRBCA_dmg_desc, mRBCA_hist_notes;
    private EditText mRBCA_struct_type_oth,mRBCA_struct_notes,mRBCA_found_type_oth,mRBCA_actn_oth,mRBCA_eval_oth;
    private EditText mRBCA_found_notes, mRBCA_extwall_mat_oth,mRBCA_extwall_notes, mRBCA_extfeat_type_oth,mRBCA_extfeat_notes,mRBCA_win_type_oth;
    private EditText mRBCA_win_mat_oth, mRBCA_win_notes,mRBCA_roof_type_oth, mRBCA_roof_mat_oth,mRBCA_roof_notes,mRBCA_int_collect_type_oth;
    private EditText mRBCA_int_notes,mRBCA_landveg_feat_oth,mRBCA_landveg_notes,mRBCA_landblt_feat_oth,mRBCA_landblt_notes;
    private EditText mRBCA_hist_age_src_oth, mRBCA_hist_age_notes,mRBCA_hzrd_type_oth,mRBCA_hzrd_notes,mRBCA_hzrd_hazmat_oth;
    
    protected Button mRBCA_bldg_area_select,mRBCA_bldg_posting_select,mRBCA_bldg_use_select, mRBCA_hist_desig_select, mRBCA_dmg_source_select;
    protected Button mRBCA_extwall_mat_select,mRBCA_extfeat_type_select,mRBCA_win_type_select, mRBCA_struct_defects_select;
    protected Button mRBCA_win_mat_select,mRBCA_roof_type_select,mRBCA_roof_mat_select,mRBCA_int_cond_select, mRBCA_int_collect_type_select,mRBCA_landveg_feat_select;
    protected Button mRBCA_landblt_feat_select,mRBCA_hzrd_type_select,mRBCA_hzrd_hazmat_select, mRBCA_actn_select, mRBCA_eval_select;
    
    
    protected CharSequence[] AreaAssessed = { "Exterior", "Interior" };
    protected CharSequence[] PostingChoices = {"Inspected", "Restricted Use", "Unsafe", "Further Evaluation","Other"};
    protected CharSequence[] usesChoices = { "Residential", "Apartment Bldg.", "Retail", "Offices", "Industrial", "Government",
                                                "Museum" , "School" , "Religious" , "Cemetery" , "Library/Archive",
                                                "Hospital/Health Care", "Farming", "Military", "Other"};
    protected CharSequence[] desigChoices = {"National Landmark","NR Listed", "State", "Other"};
    protected CharSequence[] dmgSourceChoices = {"Earth Movement","Fire", "Flood/Water", "Snow/Ice","Wind","Chemical","Explosion","Other"};
    protected CharSequence[] floodWaterChoices = {"Standing","Flowing","Ground Water","Water Marks","Other"};
    protected CharSequence[] floodEntryChoices = {"Basement/Crawl","Other"};
    protected CharSequence[] floodSedChoices = {"Deposit","Eroded","Unknown","None","Other"};
    protected CharSequence[] struct_defects_choices = {"Racking/Movement","Off Foundation","Partial Collapse","Inminent Collapse","Total Collapse"};
    protected CharSequence[] extwall_mat_choices = {"Wood","Vinyl","Masonry","Aluminum","Metal(non aluminum)","Asphalt Siding","Stucco/Plaster","Cememtitious Siding","Other"};
    protected CharSequence[] extfeat_type_choices = {"Decorative Elements","Porch/Verandah/Deck","Balcony","Gutters/Downspouts","Other"};
    protected CharSequence[] win_type_choices = {"Fixed","Double/Single-hung","Casement","Sliding","Hopper","Awning","Pivot","Louver","Other"};
    protected CharSequence[] win_mat_choices = {"Wood","Steel","Aluminum","Vinyl","Other"};
    protected CharSequence[] roof_type_choices = {"Hipped","Gable","Mansard","Pyramid","Flat","Shed","Gambrel","Other"};
    protected CharSequence[] roof_mat_choices = {"Slate","Metal","Tile","Asphalt","Cement/Asbestos","Other"};
    protected CharSequence[] int_cond_choices = {"Structural Damage","Mold/Mildew","Falling Plaster","Debris Deposited"};
    protected CharSequence[] int_collect_type_choices = {"Antiques","Archives","Art Work","Other"};
    protected CharSequence[] landveg_feat_choices = {"Trees","Hedges/Shrubs","Planting Beds","Other"};
    protected CharSequence[] landblt_feat_choices = {"Pavement/Roads/Walkway","Fence","Wall","Sculpture","Fountain","Other"};
    protected CharSequence[] hzrd_type_choices = {"Racking/Structural Movement","Off Foundation","Partial Collapse","Inminent Collapse","Total Collapse","Ground Shift","Falling Hazard","Animals/Reptiles","Hazardous Materials","Other"};
    protected CharSequence[] hzrd_hazmat_choices = {"Mold/Mildew","Flood Water","Sewage","Chemicals","Biological Waste (eg. animal carcass)","Asbestos","Other"};
    protected CharSequence[] actn_choices = {"Debris Removal","Personal Protective Measures","Falling Hazard Removal","Shoring/Bracing","Barricades","Other"};
    protected CharSequence[] eval_choices = {"Structural","Geotechnical","Archeological","Collections","Determination of Eligibility","HazMat","Other"};
    
    
    protected ArrayList<CharSequence> selectedChoices = new ArrayList<CharSequence>();
    protected CharSequence[] Choices;
    protected Button strdButton;
    
    Calendar dateTime=Calendar.getInstance();
    
    ////// end surveys for android
    
    
    private Location mCurrentLocation;
    private LocationHelper mLocationHelper;
    private Handler mAutoSaveHandler;
    private JSONArray mCategories;

    private boolean mIsPage = false;
    private boolean mIsNew = false;
    private boolean mLocalDraft = false;
    private boolean mIsCustomPubDate = false;
    private boolean mIsBackspace = false;
    private boolean mScrollDetected = false;
    private boolean mIsNewDraft = false;
    private boolean mIsExternalInstance = false;

    private List<String> mSelectedCategories;
    private String mAccountName = "";
    private int mQuickMediaType = -1;
    private String mMediaCapturePath = "";

    private String[] mPostFormats = null;
    private String[] mPostFormatTitles = null;

    private int mBlogID = -1;
    private long mPostID = -1;
    private long mCustomPubDate = 0;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private int mStyleStart, mSelectionStart, mSelectionEnd;
    private int mLastPosition = -1;
    private int mCurrentActivityRequest = -1;

    private float mLastYPos = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mCategories = new JSONArray();
        mAutoSaveHandler = new Handler();
        mSelectedCategories = new Vector<String>();
        
        
        String action = getIntent().getAction();
        if (Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            // we arrived here from a share action
            mIsExternalInstance = true;
            if (!selectBlogForShareAction())
                return;
        } else {
            initBlog();
            if (extras != null) {
                mAccountName = EscapeUtils.unescapeHtml(extras.getString("accountName"));
                mPostID = extras.getLong("postID");
                mLocalDraft = extras.getBoolean("localDraft", false);
                mIsPage = extras.getBoolean("isPage", false);
                mIsNew = extras.getBoolean("isNew", false);

                if (savedInstanceState != null) {
                    mCurrentActivityRequest = savedInstanceState.getInt("currentActivityRequest");
                    if (savedInstanceState.getString("mediaCapturePath") != null)
                        mMediaCapturePath = savedInstanceState.getString("mediaCapturePath");
                } else {
                    mQuickMediaType = extras.getInt("quick-media", -1);
                }

                if (extras.getBoolean("isQuickPress")) {
                    mIsExternalInstance = true;
                    mBlogID = extras.getInt("id");
                } else {
                    mBlogID = WordPress.currentBlog.getId();
                }

                try {
                    mBlog = new Blog(mBlogID);
                    WordPress.currentBlog = mBlog;
                } catch (Exception e) {
                    showBlogErrorAndFinish();
                    return;
                }

                if (!mIsNew) {
                    try {
                        mPost = new Post(mBlogID, mPostID, mIsPage);
                        if (mPost == null) {
                            // big oopsie
                            Toast.makeText(this, getResources().getText(R.string.post_not_found), Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        } else {
                            WordPress.currentPost = mPost;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }
            }

            if (mIsNew) {
                mLocalDraft = true;
                setTitle(EscapeUtils.unescapeHtml(WordPress.currentBlog.getBlogName()) + " - "
                        + getString((mIsPage) ? R.string.new_page : R.string.new_post));
            } else {
                setTitle(EscapeUtils.unescapeHtml(WordPress.currentBlog.getBlogName()) + " - "
                        + getString((mIsPage) ? R.string.edit_page : R.string.edit_post));
            }
        }

        setContentView(R.layout.edit);
        mContentEditText = (WPEditText) findViewById(R.id.postContent);
        mTitleEditText = (EditText) findViewById(R.id.title);
        mPasswordEditText = (EditText) findViewById(R.id.post_password);
        mLocationText = (TextView) findViewById(R.id.locationText);
        mBoldToggleButton = (ToggleButton) findViewById(R.id.bold);
        mEmToggleButton = (ToggleButton) findViewById(R.id.em);
        mBquoteToggleButton = (ToggleButton) findViewById(R.id.bquote);
        mUnderlineToggleButton = (ToggleButton) findViewById(R.id.underline);
        mStrikeToggleButton = (ToggleButton) findViewById(R.id.strike);
        mCategoriesText = (TextView) findViewById(R.id.selectedCategories);
        mAddPictureButton = (ImageButton) findViewById(R.id.addPictureButton);
        mPubDateButton = (Button) findViewById(R.id.pubDateButton);
        mPubDateText = (TextView) findViewById(R.id.pubDate);
        mLinkButton = (Button) findViewById(R.id.link);
        mMoreButton = (Button) findViewById(R.id.more);
        mStatusSpinner = (Spinner) findViewById(R.id.status);
        mTagsEditText = (EditText) findViewById(R.id.tags);
        mFormatBar = (RelativeLayout) findViewById(R.id.formatBar);
        
        
        ////////////////////////added Surveys for Android
       
        mRBCA_coord_loc_spinner = (Spinner) findViewById(R.id.rbca_coord_loc_spinner);
        mRBCA_coord_loc_oth = (EditText) findViewById(R.id.rbca_coord_loc_oth);
        mRBCA_coord_corner_spinner = (Spinner) findViewById(R.id.rbca_coord_corner_spinner);
        mRBCA_coord_notes = (EditText) findViewById(R.id.rbca_coord_notes);
        mRBCA_addr_no = (EditText) findViewById(R.id.rbca_addr_no);
        mRBCA_addr_street = (EditText) findViewById(R.id.rbca_addr_street);
        mRBCA_bldg_area_select = (Button) findViewById(R.id.rbca_bldg_area_select);
        mRBCA_bldg_posting_select = (Button) findViewById(R.id.rbca_bldg_posting_select);
        mRBCA_bldg_posting_oth = (EditText) findViewById(R.id.rbca_bldg_posting_oth);
        mRBCA_bldg_occucy_spinner = (Spinner) findViewById(R.id.rbca_bldg_occucy);
        mRBCA_bldg_occucy_avail = (ToggleButton) findViewById(R.id.rbca_bldg_occucy_available);
        mRBCA_bldg_stories = (EditText) findViewById(R.id.rbca_bldg_stories);
        mRBCA_bldg_width = (EditText) findViewById(R.id.rbca_bldg_width);
        mRBCA_bldg_length = (EditText) findViewById(R.id.rbca_bldg_length);
        mRBCA_bldg_use_select = (Button) findViewById(R.id.rbca_bldg_use_select);
        mRBCA_bldg_use_oth = (EditText) findViewById(R.id.rbca_bldg_use_oth);
        mRBCA_bldg_outbldg = (ToggleButton) findViewById(R.id.rbca_bldg_outbldg);
        mRBCA_bldg_outbldg_notes = (EditText) findViewById(R.id.rbca_bldg_outbldg_notes);
        mRBCA_bldg_units_res = (EditText) findViewById(R.id.rbca_bldg_units_res);
        mRBCA_bldg_units_comm = (EditText) findViewById(R.id.rbca_bldg_units_comm);
        mRBCA_bldg_occu_name = (EditText) findViewById(R.id.rbca_bldg_occu_name);
        mRBCA_bldg_occu_phone = (EditText) findViewById(R.id.rbca_bldg_occu_phone);
        mRBCA_bldg_notes = (EditText) findViewById(R.id.rbca_bldg_notes);
        mRBCA_hist_desig_select = (Button) findViewById(R.id.rbca_hist_desig_select);
        mRBCA_hist_desig_oth = (EditText) findViewById(R.id.rbca_hist_desig_oth);
        mRBCA_hist_dist_spinner = (Spinner) findViewById(R.id.rbca_hist_dist);
        mRBCA_hist_dist_name = (EditText) findViewById(R.id.rbca_hist_dist_name);
        mRBCA_hist_appear = (ToggleButton) findViewById(R.id.rbca_hist_appear);
        mRBCA_hist_age_spinner = (Spinner) findViewById(R.id.rbca_hist_age);
        mRBCA_hist_age_meta_spinner = (Spinner) findViewById(R.id.rbca_hist_age_meta);
        mRBCA_hist_yr_built = (EditText) findViewById(R.id.rbca_hist_yr_built);
        mRBCA_hist_age_src_spinner = (Spinner) findViewById(R.id.rbca_hist_age_src);
        mRBCA_hist_age_src_oth = (EditText) findViewById(R.id.rbca_hist_age_src_oth);
        mRBCA_hist_notes = (EditText) findViewById(R.id.rbca_hist_notes);
        mRBCA_dmg_source_select = (Button) findViewById(R.id.rbca_dmg_source_select);
        mRBCA_dmg_source_oth = (EditText) findViewById(R.id.rbca_dmg_source_oth);
        mRBCA_dmg_total_spinner = (Spinner) findViewById(R.id.rbca_dmg_total);
        mRBCA_dmg_desc = (EditText) findViewById(R.id.rbca_dmg_desc); 
        mRBCA_struct_type_spinner = (Spinner) findViewById(R.id.rbca_struct_type);
        mRBCA_struct_type_oth = (EditText) findViewById(R.id.rbca_struct_type_oth);
        mRBCA_struct_defects_select = (Button) findViewById(R.id.rbca_struct_defects_select);
        mRBCA_struct_spinner = (Spinner) findViewById(R.id.rbca_struct);
        mRBCA_struct_notes = (EditText) findViewById(R.id.rbca_struct_notes);
        mRBCA_found_type_spinner = (Spinner) findViewById(R.id.rbca_found_type);
        mRBCA_found_type_oth = (EditText) findViewById(R.id.rbca_found_type_oth);
        mRBCA_found_spinner = (Spinner) findViewById(R.id.rbca_found);
        mRBCA_found_notes = (EditText) findViewById(R.id.rbca_found_notes);
        mRBCA_extwall_mat_select = (Button) findViewById(R.id.rbca_extwall_mat_select);
        mRBCA_extwall_mat_oth = (EditText) findViewById(R.id.rbca_extwall_mat_oth);
        mRBCA_extwall_spinner = (Spinner) findViewById(R.id.rbca_extwall);
        mRBCA_extwall_notes = (EditText) findViewById(R.id.rbca_extwall_notes);
        mRBCA_extfeat_type_select = (Button) findViewById(R.id.rbca_extfeat_type_select);
        mRBCA_extfeat_type_oth = (EditText) findViewById(R.id.rbca_extfeat_type_oth);
        mRBCA_extfeat_spinner = (Spinner) findViewById(R.id.rbca_extfeat);
        mRBCA_extfeat_notes = (EditText) findViewById(R.id.rbca_extfeat_notes);
        mRBCA_win_type_select = (Button) findViewById(R.id.rbca_win_type_select);
        mRBCA_win_type_oth = (EditText) findViewById(R.id.rbca_win_type_oth);
        mRBCA_win_mat_select = (Button) findViewById(R.id.rbca_win_mat_select);
        mRBCA_win_mat_oth = (EditText) findViewById(R.id.rbca_win_mat_oth);
        mRBCA_win_spinner = (Spinner) findViewById(R.id.rbca_win);
        mRBCA_win_notes = (EditText) findViewById(R.id.rbca_win_notes);
        mRBCA_roof_type_select = (Button) findViewById(R.id.rbca_roof_type_select);
        mRBCA_roof_type_oth = (EditText) findViewById(R.id.rbca_roof_type_oth);
        mRBCA_roof_mat_select = (Button) findViewById(R.id.rbca_roof_mat_select);
        mRBCA_roof_mat_oth = (EditText) findViewById(R.id.rbca_roof_mat_oth);
        mRBCA_roof_spinner = (Spinner) findViewById(R.id.rbca_roof);
        mRBCA_roof_notes = (EditText) findViewById(R.id.rbca_roof_notes);
        mRBCA_int_cond_select = (Button) findViewById(R.id.rbca_int_cond_select);
        mRBCA_int_collect_extant_spinner = (Spinner) findViewById(R.id.rbca_int_collect_extant);
        mRBCA_int_collect_type_select = (Button) findViewById(R.id.rbca_int_collect_type_select);
        mRBCA_int_collect_type_oth = (EditText) findViewById(R.id.rbca_int_collect_type_oth);
        mRBCA_int_notes = (EditText) findViewById(R.id.rbca_int_notes);
        mRBCA_landveg_feat_select = (Button) findViewById(R.id.rbca_landveg_feat_select);
        mRBCA_landveg_feat_oth = (EditText) findViewById(R.id.rbca_landveg_feat_oth);
        mRBCA_landveg_spinner = (Spinner) findViewById(R.id.rbca_landveg);
        mRBCA_landveg_notes = (EditText) findViewById(R.id.rbca_landveg_notes);
        mRBCA_landblt_feat_select = (Button) findViewById(R.id.rbca_landblt_feat_select);
        mRBCA_landblt_feat_oth = (EditText) findViewById(R.id.rbca_landblt_feat_oth);
        mRBCA_landblt_spinner = (Spinner) findViewById(R.id.rbca_landblt);
        mRBCA_landblt_notes = (EditText) findViewById(R.id.rbca_landblt_notes);
        mRBCA_hzrd_spinner = (Spinner) findViewById(R.id.rbca_hzrd);
        mRBCA_hzrd_type_select = (Button) findViewById(R.id.rbca_hzrd_type_select);
        mRBCA_hzrd_type_oth = (EditText) findViewById(R.id.rbca_hzrd_type_oth);
        mRBCA_hzrd_notes = (EditText) findViewById(R.id.rbca_hzrd_notes);
        mRBCA_hzrd_hazmat_select = (Button) findViewById(R.id.rbca_hzrd_hazmat_select);
        mRBCA_hzrd_hazmat_oth = (EditText) findViewById(R.id.rbca_hzrd_hazmat_oth);
        mRBCA_actn_select = (Button) findViewById(R.id.rbca_actn_select);
        mRBCA_actn_oth = (EditText) findViewById(R.id.rbca_actn_oth);
        mRBCA_eval_select = (Button) findViewById(R.id.rbca_eval_select);
        mRBCA_eval_oth = (EditText) findViewById(R.id.rbca_eval_oth);
       
        //////////////////////////end Surveys for Android
        
        

        // Set header labels to upper case
        ((TextView) findViewById(R.id.statusLabel)).setText(getResources().getString(R.string.status).toUpperCase());
        ((TextView) findViewById(R.id.postFormatLabel)).setText(getResources().getString(R.string.post_format).toUpperCase());
        ((TextView) findViewById(R.id.pubDateLabel)).setText(getResources().getString(R.string.publish_date).toUpperCase());
        
        //////////////////added Surveys for Android
        ((TextView) findViewById(R.id.title_label)).setText(getResources().getString(R.string.title).toUpperCase());
        ((TextView) findViewById(R.id.rbca_coord_loc_label)).setText(getResources().getString(R.string.rbca_coord_loc).toUpperCase());
        ((TextView) findViewById(R.id.rbca_coord_loc_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_coord_notes_label)).setText(getResources().getString(R.string.rbca_coord_notes_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_coord_corner_label)).setText(getResources().getString(R.string.rbca_coord_corner_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_addr_no_label)).setText(getResources().getString(R.string.rbca_addr_no_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_addr_street_label)).setText(getResources().getString(R.string.rbca_addr_street_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_area_label)).setText(getResources().getString(R.string.rbca_bldg_area_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_posting_label)).setText(getResources().getString(R.string.rbca_bldg_posting_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_posting_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_occucy_label)).setText(getResources().getString(R.string.rbca_bldg_occucy_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_occucy_available_label)).setText(getResources().getString(R.string.rbca_bldg_occucy_available_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_stories_label)).setText(getResources().getString(R.string.rbca_bldg_stories_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_width_label)).setText(getResources().getString(R.string.rbca_bldg_width_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_length_label)).setText(getResources().getString(R.string.rbca_bldg_length_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_use_label)).setText(getResources().getString(R.string.rbca_bldg_use_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_use_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_outbldg_label)).setText(getResources().getString(R.string.rbca_bldg_outbldg_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_outbldg_notes_label)).setText(getResources().getString(R.string.rbca_bldg_outbldg_notes_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_units_res_label)).setText(getResources().getString(R.string.rbca_bldg_units_res_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_units_comm_label)).setText(getResources().getString(R.string.rbca_bldg_units_comm_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_occu_name_label)).setText(getResources().getString(R.string.rbca_bldg_occu_name_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_occu_phone_label)).setText(getResources().getString(R.string.rbca_bldg_occu_phone_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_bldg_notes_label)).setText(getResources().getString(R.string.rbca_bldg_notes_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_hist_desig_label)).setText(getResources().getString(R.string.rbca_hist_desig_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_hist_desig_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_hist_dist_label)).setText(getResources().getString(R.string.rbca_hist_dist_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_hist_dist_name_label)).setText(getResources().getString(R.string.rbca_hist_dist_name_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_hist_appear_label)).setText(getResources().getString(R.string.rbca_hist_appear_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_hist_age_label)).setText(getResources().getString(R.string.rbca_hist_age_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_hist_age_meta_label)).setText(getResources().getString(R.string.rbca_hist_age_meta_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_hist_yr_built_label)).setText(getResources().getString(R.string.rbca_hist_yr_built_label).toUpperCase());
       // ((TextView) findViewById(R.id.rbca_dmg_date_label)).setText(getResources().getString(R.string.rbca_dmg_date_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_dmg_source_label)).setText(getResources().getString(R.string.rbca_dmg_source_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_dmg_source_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_dmg_total_label)).setText(getResources().getString(R.string.rbca_dmg_total_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_dmg_desc_label)).setText(getResources().getString(R.string.rbca_dmg_desc_label).toUpperCase());
//        ((TextView) findViewById(R.id.rbca_flood_water_label)).setText(getResources().getString(R.string.rbca_flood_water_label).toUpperCase());
//        ((TextView) findViewById(R.id.rbca_flood_water_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
//        ((TextView) findViewById(R.id.rbca_flood_entry_label)).setText(getResources().getString(R.string.rbca_flood_entry_label).toUpperCase());
//        ((TextView) findViewById(R.id.rbca_flood_entry_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
//        ((TextView) findViewById(R.id.rbca_flood_depth_label)).setText(getResources().getString(R.string.rbca_flood_depth_label).toUpperCase());
//        ((TextView) findViewById(R.id.rbca_flood_sed_label)).setText(getResources().getString(R.string.rbca_flood_sed_label).toUpperCase());
//        ((TextView) findViewById(R.id.rbca_flood_sed_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
//        ((TextView) findViewById(R.id.rbca_flood_notes_label)).setText(getResources().getString(R.string.rbca_flood_notes_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_struct_type_label)).setText(getResources().getString(R.string.rbca_struct_type_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_struct_type_oth_label)).setText(getResources().getString(R.string.rbca_other_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_struct_label)).setText(getResources().getString(R.string.rbca_struct_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_struct_notes_label)).setText(getResources().getString(R.string.rbca_struct_notes_label).toUpperCase());
        ((TextView) findViewById(R.id.rbca_found_type_label)).setText(getResources().getString(R.string.rbca_found_type_label).toUpperCase());
        /////////////
        
        
        if (mIsPage) { // remove post specific views
            ((LinearLayout) findViewById(R.id.section2)).setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.section3)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.postFormatLabel)).setVisibility(View.GONE);
            ((Spinner) findViewById(R.id.postFormat)).setVisibility(View.GONE);
        } else {
            if (mBlog.getPostFormats().equals("")) {
                List<Object> args = new Vector<Object>();
                args.add(mBlog);
                args.add(this);
                new ApiHelper.getPostFormatsTask().execute(args);
                mPostFormatTitles = getResources().getStringArray(R.array.post_formats_array);
                String defaultPostFormatTitles[] = { "aside", "audio", "chat", "gallery", "image", "link", "quote", "standard", "status",
                        "video" };
                mPostFormats = defaultPostFormatTitles;
            } else {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, String>>(){}.getType();
                    Map<String, String> jsonPostFormats = gson.fromJson(mBlog.getPostFormats(), type);
                    mPostFormats = new String[jsonPostFormats.size()];
                    mPostFormatTitles = new String[jsonPostFormats.size()];
                    int i = 0;
                    for (Map.Entry<String, String> entry : jsonPostFormats.entrySet()) {
                        String key = entry.getKey();
                        String val = entry.getValue();
                        mPostFormats[i] = key;
                        mPostFormatTitles[i] = val;
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Spinner pfSpinner = (Spinner) findViewById(R.id.postFormat);
            ArrayAdapter<String> pfAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mPostFormatTitles);
            pfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pfSpinner.setAdapter(pfAdapter);
            String activePostFormat = "standard";
            if (!mIsNew) {
                try {
                    if (!mPost.getWP_post_format().equals(""))
                        activePostFormat = mPost.getWP_post_format();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < mPostFormats.length; i++) {
                if (mPostFormats[i].equals(activePostFormat))
                    pfSpinner.setSelection(i);
            }

            if (Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action))
                setContent();
        }

        //Surveys for Android/////////////////  ARRAY ADAPTERS FOR SPINNERS 
        String[] coord_loc = new String[] { getResources().getString(R.string.rbca_entrance_label), getResources().getString(R.string.rbca_corner_label),
                getResources().getString(R.string.rbca_other_label)};

        ArrayAdapter<String> coord_loc_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coord_loc);
        coord_loc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRBCA_coord_loc_spinner.setAdapter(coord_loc_adapter);
        
        String[] coord_corner = new String[] { getResources().getString(R.string.N), getResources().getString(R.string.NE),
                getResources().getString(R.string.E),getResources().getString(R.string.SE),getResources().getString(R.string.S),
                getResources().getString(R.string.SW),getResources().getString(R.string.W),getResources().getString(R.string.NW)};

        ArrayAdapter<String> coord_corner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coord_corner);
        coord_corner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRBCA_coord_corner_spinner.setAdapter(coord_corner_adapter);
        
        
        
        String[] occupancy = new String[] { getResources().getString(R.string.Occupied), getResources().getString(R.string.Vacant),
                getResources().getString(R.string.unknown)};

        ArrayAdapter<String> occupancyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, occupancy);
        occupancyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRBCA_bldg_occucy_spinner.setAdapter(occupancyAdapter);
        
        String[] hist_dist = new String[] { getResources().getString(R.string.yes), getResources().getString(R.string.no),
                getResources().getString(R.string.unknown)};

        ArrayAdapter<String> histDistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hist_dist);
        histDistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRBCA_hist_dist_spinner.setAdapter(histDistAdapter);
        
        String[] hist_age = new String[] { getResources().getString(R.string.rbca_hist_age_lt50_label), getResources().getString(R.string.rbca_hist_age_betfifty100_label),
                getResources().getString(R.string.rbca_hist_age_gt100_label)};

        ArrayAdapter<String> histAgeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hist_age);
        histAgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRBCA_hist_age_spinner.setAdapter(histAgeAdapter);
        
        String[] hist_age_meta = new String[] { getResources().getString(R.string.estimated), getResources().getString(R.string.verified),
                getResources().getString(R.string.reported)};

        ArrayAdapter<String> histAgeMetaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hist_age_meta);
        histAgeMetaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRBCA_hist_age_meta_spinner.setAdapter(histAgeMetaAdapter);
        
        
        
        //end Surverys for Android ///////////////////////
        
        
        String[] items = new String[] { getResources().getString(R.string.publish_post), getResources().getString(R.string.draft),
                getResources().getString(R.string.pending_review), getResources().getString(R.string.post_private),
                getResources().getString(R.string.local_draft) };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatusSpinner.setAdapter(adapter);

        getLocationProvider();

        if (mIsNew) {
            if (mQuickMediaType >= 0) {
                // User selected a 'Quick (media type)' option in the menu drawer
                if (mQuickMediaType == Constants.QUICK_POST_PHOTO_CAMERA)
                    launchCamera();
                else if (mQuickMediaType == Constants.QUICK_POST_PHOTO_LIBRARY)
                    launchPictureLibrary();
                else if (mQuickMediaType == Constants.QUICK_POST_VIDEO_CAMERA)
                    launchVideoCamera();
                else if (mQuickMediaType == Constants.QUICK_POST_VIDEO_LIBRARY)
                    launchVideoLibrary();
                mLocalDraft = extras.getBoolean("localDraft");
            }
        } else {
            mTitleEditText.setText(mPost.getTitle());

            if (mPost.isUploaded()) {
                items = new String[] {
                        getResources().getString(R.string.publish_post),
                        getResources().getString(R.string.draft),
                        getResources().getString(R.string.pending_review),
                        getResources().getString(R.string.post_private)
                };
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                        items);
                mStatusSpinner.setAdapter(adapter);
            }

            String contentHTML;

            if (!mPost.getMt_text_more().equals("")) {
                if (mPost.isLocalDraft())
                    contentHTML = mPost.getDescription() + "\n&lt;!--more--&gt;\n"
                            + mPost.getMt_text_more();
                else
                    contentHTML = mPost.getDescription() + "\n<!--more-->\n"
                            + mPost.getMt_text_more();
            } else
                contentHTML = mPost.getDescription();

            try {
                if (mPost.isLocalDraft())
                    mContentEditText.setText(WPHtml.fromHtml(contentHTML.replaceAll("\uFFFC", ""),
                            EditPostActivity.this, mPost));
                else
                    mContentEditText.setText(contentHTML.replaceAll("\uFFFC", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }

            long pubDate = mPost.getDate_created_gmt();
            if (pubDate != 0) {
                try {
                    int flags = 0;
                    flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                    flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                    flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;
                    flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                    String formattedDate = DateUtils.formatDateTime(EditPostActivity.this, pubDate,
                            flags);
                    mPubDateText.setText(formattedDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (mPost.getWP_password() != null)
                mPasswordEditText.setText(mPost.getWP_password());
            
            
            
            ///////////////////SURVEYS FOR ANDROID ///////////////
            if (mPost.getRBCA_coord_loc() != null){
                String rbca_coord_loc = mPost.getRBCA_coord_loc();
                
                if (rbca_coord_loc.equals("Entrance")) {
                    mRBCA_coord_loc_spinner.setSelection(0, true);
                } else if (rbca_coord_loc.equals("Corner")) {
                    mRBCA_coord_loc_spinner.setSelection(1, true);
                } else if (rbca_coord_loc.equals("Other")) {
                    mRBCA_coord_loc_spinner.setSelection(2, true);
                } 
            }
            
            if (mPost.getRBCA_coord_corner() != null){
                String rbca_coord_corner = mPost.getRBCA_coord_corner();
                
                if (rbca_coord_corner.equals("N")) {
                    mRBCA_coord_corner_spinner.setSelection(0, true);
                } else if (rbca_coord_corner.equals("NE")) {
                    mRBCA_coord_corner_spinner.setSelection(1, true);
                } else if (rbca_coord_corner.equals("E")) {
                    mRBCA_coord_corner_spinner.setSelection(2, true);
                } else if (rbca_coord_corner.equals("SE")) {
                    mRBCA_coord_corner_spinner.setSelection(3, true);
                } else if (rbca_coord_corner.equals("S")) {
                    mRBCA_coord_corner_spinner.setSelection(4, true);
                } else if (rbca_coord_corner.equals("SW")) {
                    mRBCA_coord_corner_spinner.setSelection(5, true);
                } else if (rbca_coord_corner.equals("W")) {
                    mRBCA_coord_corner_spinner.setSelection(6, true);
                } else if (rbca_coord_corner.equals("NW")) {
                    mRBCA_coord_corner_spinner.setSelection(7, true);
                } 
            }
            
            if (mPost.getRBCA_coord_loc_oth() != null){
                mRBCA_coord_loc_oth.setText(mPost.getRBCA_coord_loc_oth());
            }
            
            if (mPost.getRBCA_coord_notes() != null){
                mRBCA_coord_notes.setText(mPost.getRBCA_coord_notes()) ;
            }
            
            if (mPost.getRBCA_addr_no() != null){
                mRBCA_addr_no.setText(mPost.getRBCA_addr_no()) ;
            }
            
            if (mPost.getRBCA_addr_street() != null){
                mRBCA_addr_street.setText(mPost.getRBCA_addr_street()) ;
            }
            
            if (mPost.getRBCA_bldg_area() != null){
                mRBCA_bldg_area_select.setText(mPost.getRBCA_bldg_area()) ;
            }
            
            if (mPost.getRBCA_bldg_posting() != null){
                mRBCA_bldg_posting_select.setText(mPost.getRBCA_bldg_posting()) ;
            }
            
            if (mPost.getRBCA_bldg_posting_oth() != null){
                mRBCA_bldg_posting_oth.setText(mPost.getRBCA_bldg_posting_oth()) ;
            }
            
            if (mPost.getRBCA_bldg_occucy() !=null){
                String occupancy1 = mPost.getRBCA_bldg_occucy();
                if (occupancy1.equals("Occupied")) {
                    mRBCA_bldg_occucy_spinner.setSelection(0, true);
                } else if (occupancy1.equals("Vacant")) {
                    mRBCA_bldg_occucy_spinner.setSelection(1, true);
                } else if (occupancy1.equals("Unknown")) {
                    mRBCA_bldg_occucy_spinner.setSelection(2, true);
                }
            }
            
            int occucyAvailable = mPost.getRBCA_bldg_occucy_avail();
            if (occucyAvailable == 1)
                mRBCA_bldg_occucy_avail.toggle();
            
            if (mPost.getRBCA_bldg_stories() != 0.0){
                mRBCA_bldg_stories.setText(Double.toString(mPost.getRBCA_bldg_stories()));}
            
            if (mPost.getRBCA_bldg_width() != 0.0){
                mRBCA_bldg_width.setText(Double.toString(mPost.getRBCA_bldg_width()));}
            
            if (mPost.getRBCA_bldg_length() != 0.0){
                mRBCA_bldg_length.setText(Double.toString(mPost.getRBCA_bldg_length()));}
            
            if (mPost.getRBCA_bldg_use() != null){
                mRBCA_bldg_use_select.setText(mPost.getRBCA_bldg_use());}
            
            if (mPost.getRBCA_bldg_use_oth() != null){
                mRBCA_bldg_use_oth.setText(mPost.getRBCA_bldg_use_oth());}
            
            int outbldg = mPost.getRBCA_bldg_outbldg();
            if (outbldg == 1){
                mRBCA_bldg_outbldg.toggle();}
            
            if (mPost.getRBCA_bldg_outbldg_notes() != null){
                mRBCA_bldg_outbldg_notes.setText(mPost.getRBCA_bldg_outbldg_notes());}
            
            if (mPost.getRBCA_bldg_units_res() != 0){
                mRBCA_bldg_units_res.setText(Integer.toString(mPost.getRBCA_bldg_units_res()));}
            
            if (mPost.getRBCA_bldg_units_comm() != 0){
                mRBCA_bldg_units_comm.setText(Integer.toString(mPost.getRBCA_bldg_units_comm()));}
            
            if (mPost.getRBCA_bldg_occu_name() != null){
                mRBCA_bldg_occu_name.setText(mPost.getRBCA_bldg_occu_name());}
            
            if (mPost.getRBCA_bldg_occu_phone() != 0){
                mRBCA_bldg_occu_phone.setText(Integer.toString(mPost.getRBCA_bldg_occu_phone()));}
            
            if (mPost.getRBCA_bldg_notes() != null){
                mRBCA_bldg_notes.setText(mPost.getRBCA_bldg_notes());}
            
            if (mPost.getRBCA_hist_desig() != null){
                mRBCA_hist_desig_select.setText(mPost.getRBCA_hist_desig());}
            
            if (mPost.getRBCA_hist_desig_oth() != null){
                mRBCA_hist_desig_oth.setText(mPost.getRBCA_hist_desig_oth());}
            
            if (mPost.getRBCA_hist_dist() !=null){
                String hist_dist1 = mPost.getRBCA_hist_dist();
                if (hist_dist1.equals("Yes")) {
                    mRBCA_hist_dist_spinner.setSelection(0, true);
                } else if (hist_dist1.equals("No")) {
                    mRBCA_hist_dist_spinner.setSelection(1, true);
                } else if (hist_dist1.equals("Unknown")) {
                    mRBCA_hist_dist_spinner.setSelection(2, true);
                }
            }
            
            if (mPost.getRBCA_hist_dist_name() != null){
                mRBCA_hist_dist_name.setText(mPost.getRBCA_hist_dist_name());}
            
            int hist_appear = mPost.getRBCA_hist_appear();
            if (hist_appear == 1)
                mRBCA_hist_appear.toggle();
            
            if (mPost.getRBCA_hist_age() > 0 ){
                int hist_age1 = mPost.getRBCA_hist_age();
                switch (hist_age1){
                    case 1:
                        mRBCA_hist_age_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_hist_age_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_hist_age_spinner.setSelection(2);
                        break;
                }
            }
            
            if (mPost.getRBCA_hist_age_meta() !=null){
                String hist_age_meta1 = mPost.getRBCA_hist_age_meta();
                if (hist_age_meta1.equals("Estimated")) {
                    mRBCA_hist_age_meta_spinner.setSelection(0, true);
                } else if (hist_age_meta1.equals("Verified")) {
                    mRBCA_hist_age_meta_spinner.setSelection(1, true);
                } else if (hist_age_meta1.equals("Reported")) {
                    mRBCA_hist_age_meta_spinner.setSelection(2, true);
                }
            }

            if (mPost.getRBCA_hist_yr_built() != 0){
                mRBCA_hist_yr_built.setText(Integer.toString(mPost.getRBCA_hist_yr_built()));}
            
           
            if (mPost.getRBCA_hist_age_src() > 0 ){
                int hist_age_src1 = mPost.getRBCA_hist_age_src();
                switch (hist_age_src1){
                    case 1:
                        mRBCA_hist_age_src_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_hist_age_src_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_hist_age_src_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_hist_age_src_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_hist_age_src_oth() != null){
                mRBCA_hist_age_src_oth.setText(mPost.getRBCA_hist_age_src_oth());}
            
            System.out.println(mPost.getRBCA_hist_notes());
            if (mPost.getRBCA_hist_notes() != null){
                //mRBCA_hist_age_notes.setText(mPost.getRBCA_hist_notes());
                }
            
            if (mPost.getRBCA_dmg_source() != null){
                mRBCA_dmg_source_select.setText(mPost.getRBCA_dmg_source());
            }
            
            if (mPost.getRBCA_dmg_source_oth() != null){
                mRBCA_dmg_source_oth.setText(mPost.getRBCA_dmg_source_oth());
            }
            
            if (mPost.getRBCA_dmg_total() > 0 ){
                int dmg_total1 = mPost.getRBCA_dmg_total();
                switch (dmg_total1){
                    case 1:
                        mRBCA_dmg_total_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_dmg_total_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_dmg_total_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_dmg_total_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_dmg_desc() != null){
                mRBCA_dmg_desc.setText(mPost.getRBCA_dmg_desc());}
            
            // this part was deleted in revision 7./////////////////
//            if (mPost.getRBCA_flood_water() != null){
//                mRBCA_flood_water_select.setText(mPost.getRBCA_flood_water());}
//            
//            if (mPost.getRBCA_flood_water_oth() != null){
//                mRBCA_flood_water_oth.setText(mPost.getRBCA_flood_water_oth());}
//            
//            if (mPost.getRBCA_flood_entry() != null ){
//                String floodEntry = mPost.getRBCA_flood_entry();
//                if (floodEntry.equals("Basement/Crawl")){mRBCA_flood_entry_spinner.setSelection(0);}
//                if (floodEntry.equals("Other")) {mRBCA_flood_entry_spinner.setSelection(1);}
//            }
//            
//            if (mPost.getRBCA_flood_entry_oth() != null){
//                mRBCA_flood_entry_oth.setText(mPost.getRBCA_flood_entry_oth());}
//            
//            if (mPost.getRBCA_flood_depth() != 0.0){
//                mRBCA_flood_depth.setText(Double.toString(mPost.getRBCA_flood_depth()));}
//            
//            if (mPost.getRBCA_flood_sed() != null){
//                mRBCA_flood_sed_select.setText(mPost.getRBCA_flood_sed());}
//            
//            if (mPost.getRBCA_flood_sed_oth() != null){
//                mRBCA_flood_sed_oth.setText(mPost.getRBCA_flood_sed_oth());}
//            
//            if (mPost.getRBCA_flood_notes() != null){
//                mRBCA_flood_notes.setText(mPost.getRBCA_flood_notes());}
            
            if (mPost.getRBCA_struct_type() != null){
                String rbca_struct_type = mPost.getRBCA_struct_type();
                
                if (rbca_struct_type.equals("Wood Frame")) {
                    mRBCA_struct_type_spinner.setSelection(0, true);
                } else if (rbca_struct_type.equals("Metal Frame")) {
                    mRBCA_struct_type_spinner.setSelection(1, true);
                } else if (rbca_struct_type.equals("Masonry")) {
                    mRBCA_struct_type_spinner.setSelection(2, true);
                } else if (rbca_struct_type.equals("Manufactured")) {
                    mRBCA_struct_type_spinner.setSelection(3, true);
                } else if (rbca_struct_type.equals("Other")) {
                    mRBCA_struct_type_spinner.setSelection(4, true);
                } 
            }
            
            if (mPost.getRBCA_struct_type_oth() != null){
                mRBCA_struct_type_oth.setText(mPost.getRBCA_struct_type_oth());}
            
            if (mPost.getRBCA_struct_defects() != null){
                mRBCA_struct_defects_select.setText(mPost.getRBCA_struct_defects());}
            
            
            if (mPost.getRBCA_struct() > 0 ){
                int struct1 = mPost.getRBCA_struct();
                switch (struct1){
                    case 1:
                        mRBCA_struct_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_struct_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_struct_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_struct_spinner.setSelection(3);
                        break;
                }
            }
            
           
            if (mPost.getRBCA_struct_notes() != null){
                mRBCA_struct_notes.setText(mPost.getRBCA_struct_notes());}
            
            if (mPost.getRBCA_found_type() != null){
                String rbca_found_type = mPost.getRBCA_found_type();
                
                if (rbca_found_type.equals("Slab")) {
                    mRBCA_found_type_spinner.setSelection(0, true);
                } else if (rbca_found_type.equals("Basement")) {
                    mRBCA_found_type_spinner.setSelection(1, true);
                } else if (rbca_found_type.equals("Stem/Chain Wall")) {
                    mRBCA_found_type_spinner.setSelection(2, true);
                } else if (rbca_found_type.equals("Raised Pier/Post")) {
                    mRBCA_found_type_spinner.setSelection(3, true);
                } else if (rbca_found_type.equals("Other")) {
                    mRBCA_found_type_spinner.setSelection(4, true);
                }
            }
            
            if (mPost.getRBCA_found_type_oth() != null){
                mRBCA_found_type_oth.setText(mPost.getRBCA_found_type_oth());}
            
            if (mPost.getRBCA_found() > 0 ){
                int found1 = mPost.getRBCA_found();
                switch (found1){
                    case 1:
                        mRBCA_found_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_found_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_found_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_found_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_found_notes() != null){
                mRBCA_found_notes.setText(mPost.getRBCA_found_notes());}
            
            if (mPost.getRBCA_extwall_mat() != null){
                mRBCA_extwall_mat_select.setText(mPost.getRBCA_extwall_mat());}
            
            if (mPost.getRBCA_extwall_mat_oth() != null){
                mRBCA_extwall_mat_oth.setText(mPost.getRBCA_extwall_mat_oth());}
            
            if (mPost.getRBCA_extwall() > 0 ){
                int extwall1 = mPost.getRBCA_extwall();
                switch (extwall1){
                    case 1:
                        mRBCA_extwall_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_extwall_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_extwall_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_extwall_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_extfeat_type() != null){
                mRBCA_extfeat_type_select.setText(mPost.getRBCA_extfeat_type());}
            
            if (mPost.getRBCA_extfeat_type_oth() != null){
                mRBCA_extfeat_type_oth.setText(mPost.getRBCA_extfeat_type_oth());}
            
            if (mPost.getRBCA_extfeat() > 0 ){
                int extfeat1 = mPost.getRBCA_extfeat();
                switch (extfeat1){
                    case 1:
                        mRBCA_extfeat_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_extfeat_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_extfeat_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_extfeat_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_extfeat_notes() != null){
                mRBCA_extfeat_notes.setText(mPost.getRBCA_extfeat_notes());}
            
            if (mPost.getRBCA_win_type() != null){
                mRBCA_win_type_select.setText(mPost.getRBCA_win_type());}
            
            if (mPost.getRBCA_win_type_oth() != null){
                mRBCA_win_type_oth.setText(mPost.getRBCA_win_type_oth());}
            
            if (mPost.getRBCA_win_mat() != null){
                mRBCA_win_mat_select.setText(mPost.getRBCA_win_mat());}
            
            if (mPost.getRBCA_win_mat_oth() != null){
                mRBCA_win_mat_oth.setText(mPost.getRBCA_win_mat_oth());}
            
            if (mPost.getRBCA_win() > 0 ){
                int win1 = mPost.getRBCA_win();
                switch (win1){
                    case 1:
                        mRBCA_win_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_win_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_win_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_win_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_win_notes() != null){
                mRBCA_win_notes.setText(mPost.getRBCA_win_notes());}
            
            if (mPost.getRBCA_roof_type() != null){
                mRBCA_roof_type_select.setText(mPost.getRBCA_roof_type());}
            
            if (mPost.getRBCA_roof_type_oth() != null){
                mRBCA_roof_type_oth.setText(mPost.getRBCA_roof_type_oth());}
            
            if (mPost.getRBCA_roof_mat() != null){
                mRBCA_roof_mat_select.setText(mPost.getRBCA_roof_mat());}
            
            if (mPost.getRBCA_roof_mat_oth() != null){
                mRBCA_roof_mat_oth.setText(mPost.getRBCA_roof_mat_oth());}
            
            if (mPost.getRBCA_roof() > 0 ){
                int roof1 = mPost.getRBCA_roof();
                switch (roof1){
                    case 1:
                        mRBCA_roof_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_roof_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_roof_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_roof_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_roof_notes() != null){
                mRBCA_roof_notes.setText(mPost.getRBCA_roof_notes());}
            
            if (mPost.getRBCA_int_cond() != null){
                mRBCA_int_cond_select.setText(mPost.getRBCA_int_cond());}
            
            if (mPost.getRBCA_int_collect_extant() > 0 ){
                int int_collect_extant1 = mPost.getRBCA_int_collect_extant();
                switch (int_collect_extant1){
                    case 1:
                        mRBCA_int_collect_extant_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_int_collect_extant_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_int_collect_extant_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_int_collect_extant_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_int_collect_type() != null){
                mRBCA_int_collect_type_select.setText(mPost.getRBCA_int_collect_type());}
            
            if (mPost.getRBCA_int_collect_type_oth() != null){
                mRBCA_int_collect_type_oth.setText(mPost.getRBCA_int_collect_type_oth());}
            
            if (mPost.getRBCA_int_notes() != null){
                mRBCA_int_notes.setText(mPost.getRBCA_int_notes());}
            
            if (mPost.getRBCA_landveg_feat() != null){
                mRBCA_landveg_feat_select.setText(mPost.getRBCA_landveg_feat());}
            
            if (mPost.getRBCA_landveg_feat_oth() != null){
                mRBCA_landveg_feat_oth.setText(mPost.getRBCA_landveg_feat_oth());}
            
            if (mPost.getRBCA_landveg() > 0 ){
                int landveg1 = mPost.getRBCA_landveg();
                switch (landveg1){
                    case 1:
                        mRBCA_landveg_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_landveg_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_landveg_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_landveg_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_landveg_notes() != null){
                mRBCA_landveg_notes.setText(mPost.getRBCA_landveg_notes());}
            
            if (mPost.getRBCA_landblt_feat() != null){
                mRBCA_landblt_feat_select.setText(mPost.getRBCA_landblt_feat());}
            
            if (mPost.getRBCA_landblt_feat_oth() != null){
                mRBCA_landblt_feat_oth.setText(mPost.getRBCA_landblt_feat_oth());}
            
            if (mPost.getRBCA_landblt() > 0 ){
                int landblt1 = mPost.getRBCA_landblt();
                switch (landblt1){
                    case 1:
                        mRBCA_landblt_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_landblt_spinner.setSelection(1);
                        break;
                    case 3:
                        mRBCA_landblt_spinner.setSelection(2);
                        break;
                    case 4:
                        mRBCA_landblt_spinner.setSelection(3);
                        break;
                }
            }
            
            if (mPost.getRBCA_landblt_notes() != null){
                mRBCA_landblt_notes.setText(mPost.getRBCA_landblt_notes());}
            
            if (mPost.getRBCA_hzrd() > 0 ){
                int hzrd1 = mPost.getRBCA_hzrd();
                switch (hzrd1){
                    case 1:
                        mRBCA_hzrd_spinner.setSelection(0);
                        break;
                    case 2:
                        mRBCA_hzrd_spinner.setSelection(1);
                        break;
                }
            }
            
            if (mPost.getRBCA_hzrd_type() != null){
                mRBCA_hzrd_type_select.setText(mPost.getRBCA_hzrd_type());}
            
            if (mPost.getRBCA_hzrd_type_oth() != null){
                mRBCA_hzrd_type_oth.setText(mPost.getRBCA_hzrd_type_oth());}
            
            if (mPost.getRBCA_hzrd_notes() != null){
                mRBCA_hzrd_notes.setText(mPost.getRBCA_hzrd_notes());}
            
            if (mPost.getRBCA_hzrd_hazmat() != null){
                mRBCA_hzrd_hazmat_select.setText(mPost.getRBCA_hzrd_hazmat());}
            
            if (mPost.getRBCA_hzrd_hazmat_oth() != null){
                mRBCA_hzrd_hazmat_oth.setText(mPost.getRBCA_hzrd_hazmat_oth());}
            
            if (mPost.getRBCA_actn() != null){
                mRBCA_actn_select.setText(mPost.getRBCA_actn());}
            
            if (mPost.getRBCA_actn_oth() != null){
                mRBCA_actn_oth.setText(mPost.getRBCA_actn_oth());}
            
            if (mPost.getRBCA_eval() != null){
                mRBCA_eval_select.setText(mPost.getRBCA_eval());}
            
            if (mPost.getRBCA_eval_oth() != null){
                mRBCA_eval_oth.setText(mPost.getRBCA_eval_oth());}
            
            
            
            /////////////////////////end SURVEYS FOR ANDROID
            
            

            if (mPost.getPost_status() != null) {
                String status = mPost.getPost_status();

                if (status.equals("publish")) {
                    mStatusSpinner.setSelection(0, true);
                } else if (status.equals("draft")) {
                    mStatusSpinner.setSelection(1, true);
                } else if (status.equals("pending")) {
                    mStatusSpinner.setSelection(2, true);
                } else if (status.equals("private")) {
                    mStatusSpinner.setSelection(3, true);
                } else if (status.equals("localdraft")) {
                    mStatusSpinner.setSelection(4, true);
                }
            }

            if (!mIsPage) {
                if (mPost.getCategories() != null) {
                    mCategories = mPost.getCategories();
                    if (!mCategories.equals("")) {

                        for (int i = 0; i < mCategories.length(); i++) {
                            try {
                                mSelectedCategories.add(mCategories.getString(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mCategoriesText.setVisibility(View.VISIBLE);
                        mCategoriesText.setText(getString(R.string.selected_categories) + " "
                                + getCategoriesCSV());
                    }
                }

                Double latitude = mPost.getLatitude();
                Double longitude = mPost.getLongitude();

                if (latitude != 0.0) {
                    new getAddressTask().execute(latitude, longitude);
                }
            }

            String tags = mPost.getMt_keywords();
            if (!tags.equals("")) {
                mTagsEditText.setText(tags);
            }
        }

        if (!mIsPage) {
            Button selectCategories = (Button) findViewById(R.id.selectCategories);
            selectCategories.setOnClickListener(this);
        }

        registerForContextMenu(mAddPictureButton);
        mContentEditText.setOnSelectionChangedListener(this);
        mContentEditText.setOnEditTextImeBackListener(this);
        mContentEditText.setOnTouchListener(this);
        mContentEditText.addTextChangedListener(this);
        mContentEditText.setOnFocusChangeListener(this);
        mAddPictureButton.setOnClickListener(this);
        mPubDateButton.setOnClickListener(this);
        mBoldToggleButton.setOnClickListener(this);
        mLinkButton.setOnClickListener(this);
        mEmToggleButton.setOnClickListener(this);
        mUnderlineToggleButton.setOnClickListener(this);
        mStrikeToggleButton.setOnClickListener(this);
        mBquoteToggleButton.setOnClickListener(this);
        mMoreButton.setOnClickListener(this);
        
        
        ////////Moca for Android 
        mRBCA_bldg_area_select.setOnClickListener(this);
        mRBCA_bldg_posting_select.setOnClickListener(this);
        mRBCA_bldg_occucy_avail.setOnClickListener(this);
        mRBCA_bldg_use_select.setOnClickListener(this);
        mRBCA_bldg_outbldg.setOnClickListener(this);    
        mRBCA_hist_desig_select.setOnClickListener(this);
        mRBCA_hist_appear.setOnClickListener(this);
        mRBCA_dmg_source_select.setOnClickListener(this);
        mRBCA_struct_defects_select.setOnClickListener(this);
        mRBCA_extwall_mat_select.setOnClickListener(this);
        mRBCA_extfeat_type_select.setOnClickListener(this);
        mRBCA_win_type_select.setOnClickListener(this);
        mRBCA_win_mat_select.setOnClickListener(this);
        mRBCA_roof_type_select.setOnClickListener(this);
        mRBCA_roof_mat_select.setOnClickListener(this);
        mRBCA_int_cond_select.setOnClickListener(this);
        mRBCA_int_collect_type_select.setOnClickListener(this);
        mRBCA_landveg_feat_select.setOnClickListener(this);
        mRBCA_landblt_feat_select.setOnClickListener(this);
        mRBCA_hzrd_type_select.setOnClickListener(this);
        mRBCA_hzrd_hazmat_select.setOnClickListener(this);
        mRBCA_actn_select.setOnClickListener(this);
        mRBCA_eval_select.setOnClickListener(this);
        /////end Moca for android
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAutoSaveHandler != null)
            mAutoSaveHandler.postDelayed(autoSaveRunnable, 60000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationHelper != null)
            mLocationHelper.cancelTimer();

        if (mAutoSaveHandler != null)
            mAutoSaveHandler.removeCallbacks(autoSaveRunnable);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentActivityRequest", mCurrentActivityRequest);
        if (!mMediaCapturePath.equals(""))
            savedInstanceState.putString("mediaCapturePath", mMediaCapturePath);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, getResources().getText(R.string.select_photo));
        if (DeviceUtils.getInstance().hasCamera(getApplicationContext())) {
            menu.add(0, 1, 0, getResources().getText(R.string.take_photo));
        }
        menu.add(0, 2, 0, getResources().getText(R.string.select_video));
        if (DeviceUtils.getInstance().hasCamera(getApplicationContext())) {
            menu.add(0, 3, 0, getResources().getText(R.string.take_video));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
            launchPictureLibrary();
            return true;
        case 1:
            launchCamera();
            return true;
        case 2:
            launchVideoLibrary();
            return true;
        case 3:
            launchVideoCamera();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    // Menu actions
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit_post) {
            if (mAutoSaveHandler != null)
                mAutoSaveHandler.removeCallbacks(autoSaveRunnable);
            if (savePost(false)) {
                if (mPost.isUploaded() || !mPost.getPost_status().equals("localdraft")) {
                    if (mQuickMediaType >= 0) {
                        if (mQuickMediaType == Constants.QUICK_POST_PHOTO_CAMERA || mQuickMediaType == Constants.QUICK_POST_PHOTO_LIBRARY)
                            mPost.setQuickPostType("QuickPhoto");
                        else if (mQuickMediaType == Constants.QUICK_POST_VIDEO_CAMERA || mQuickMediaType == Constants.QUICK_POST_VIDEO_LIBRARY)
                            mPost.setQuickPostType("QuickVideo");
                    }
                    WordPress.currentPost = mPost;
                    startService(new Intent(this, PostUploadService.class));
                }
                Intent i = new Intent();
                i.putExtra("shouldRefresh", true);
                setResult(RESULT_OK, i);
                finish();
            }
            return true;
        } else if (itemId == android.R.id.home) {
            showCancelAlert(true);
            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && mFormatBar.getVisibility() != View.VISIBLE)
            showFormatBar();
        else if (!hasFocus && mFormatBar.getVisibility() == View.VISIBLE)
            hideFormatBar();
    }
    
    @Override
    public void onImeBack(WPEditText ctrl, String text) {
        if (mFormatBar.getVisibility() == View.VISIBLE)
            hideFormatBar();
    }
    
    private void showFormatBar() {
        mFormatBar.setVisibility(View.VISIBLE);
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeInAnimation.setDuration(500);
        mFormatBar.startAnimation(fadeInAnimation);
    }
    
    private void hideFormatBar() {
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(500);
        mFormatBar.startAnimation(fadeOutAnimation);
        mFormatBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bold) {
            formatBtnClick(mBoldToggleButton, "strong");
        } else if (id == R.id.em) {
            formatBtnClick(mEmToggleButton, "em");
        } else if (id == R.id.underline) {
            formatBtnClick(mUnderlineToggleButton, "u");
        } else if (id == R.id.strike) {
            formatBtnClick(mStrikeToggleButton, "strike");
        } else if (id == R.id.bquote) {
            formatBtnClick(mBquoteToggleButton, "blockquote");
        } else if (id == R.id.more) {
            mSelectionEnd = mContentEditText.getSelectionEnd();
            Editable str = mContentEditText.getText();
            str.insert(mSelectionEnd, "\n<!--more-->\n");
        } else if (id == R.id.link) {
            mSelectionStart = mContentEditText.getSelectionStart();
            mStyleStart = mSelectionStart;
            mSelectionEnd = mContentEditText.getSelectionEnd();
            if (mSelectionStart > mSelectionEnd) {
                int temp = mSelectionEnd;
                mSelectionEnd = mSelectionStart;
                mSelectionStart = temp;
            }
            Intent i = new Intent(EditPostActivity.this, EditLinkActivity.class);
            if (mSelectionEnd > mSelectionStart) {
                String selectedText = mContentEditText.getText().subSequence(mSelectionStart, mSelectionEnd).toString();
                i.putExtra("selectedText", selectedText);
            }
            startActivityForResult(i, ACTIVITY_REQUEST_CODE_CREATE_LINK);
        } else if (id == R.id.addPictureButton) {
            mAddPictureButton.performLongClick();
        } else if (id == R.id.pubDateButton) {
            showDialog(ID_DIALOG_DATE);
        } else if (id == R.id.selectCategories) {
            Bundle bundle = new Bundle();
            bundle.putInt("id", mBlogID);
            if (mCategories.length() > 0) {
                bundle.putString("categoriesCSV", getCategoriesCSV());
            }
            Intent i1 = new Intent(EditPostActivity.this, SelectCategoriesActivity.class);
            i1.putExtras(bundle);
            startActivityForResult(i1, ACTIVITY_REQUEST_CODE_SELECT_CATEGORIES);
        } else if (id == R.id.post) {
            if (mAutoSaveHandler != null)
                mAutoSaveHandler.removeCallbacks(autoSaveRunnable);
            if (savePost(false)) {
                if (mPost.isUploaded() || !mPost.getPost_status().equals("localdraft")) {
                    if (mQuickMediaType >= 0) {
                        if (mQuickMediaType == Constants.QUICK_POST_PHOTO_CAMERA || mQuickMediaType == Constants.QUICK_POST_PHOTO_LIBRARY)
                            mPost.setQuickPostType("QuickPhoto");
                        else if (mQuickMediaType == Constants.QUICK_POST_VIDEO_CAMERA || mQuickMediaType == Constants.QUICK_POST_VIDEO_LIBRARY)
                            mPost.setQuickPostType("QuickVideo");
                    }
                    
                    WordPress.currentPost = mPost;
                    startService(new Intent(this, PostUploadService.class));
                }
                finish();
            }
        } else if (id == R.id.viewMap) {
            Double latitude = 0.0;
            try {
                latitude = mCurrentLocation.getLatitude();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (latitude != 0.0) {
                String uri = "geo:" + latitude + "," + mCurrentLocation.getLongitude();
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            } else {
                Toast.makeText(EditPostActivity.this, getResources().getText(R.string.location_toast), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.updateLocation) {
            mLocationHelper.getLocation(EditPostActivity.this, locationResult);
        } else if (id == R.id.removeLocation) {
            if (mCurrentLocation != null) {
                mCurrentLocation.setLatitude(0.0);
                mCurrentLocation.setLongitude(0.0);
            }
            if (mPost != null) {
                mPost.setLatitude(0.0);
                mPost.setLongitude(0.0);
            }
            mLocationText.setText("");
        } else if (id == R.id.rbca_bldg_area_select){////Moca for Android////////////////////////////////////////
            showSelectionDialog((String) mRBCA_bldg_area_select.getText(),AreaAssessed,mRBCA_bldg_area_select);
        } else if (id == R.id.rbca_bldg_posting_select){
            showSelectionDialog((String) mRBCA_bldg_posting_select.getText(),PostingChoices,mRBCA_bldg_posting_select);
        } else if (id == R.id.rbca_bldg_occucy_available){
            onChangeToggleButton(mRBCA_bldg_occucy_avail);
        } else if (id == R.id.rbca_bldg_use_select){
            showSelectionDialog((String) mRBCA_bldg_use_select.getText(),usesChoices,mRBCA_bldg_use_select);
        } else if (id == R.id.rbca_bldg_outbldg){
            onChangeToggleButton(mRBCA_bldg_outbldg);
        } else if (id == R.id.rbca_hist_desig_select){
            showSelectionDialog((String) mRBCA_hist_desig_select.getText(), desigChoices,mRBCA_hist_desig_select);
        } else if (id == R.id.rbca_hist_appear){
            onChangeToggleButton(mRBCA_hist_appear);
        } else if (id == R.id.rbca_struct_defects_select){
            showSelectionDialog((String) mRBCA_struct_defects_select.getText(),struct_defects_choices,mRBCA_struct_defects_select);
        } else if (id == R.id.rbca_extwall_mat_select){
            showSelectionDialog((String) mRBCA_extwall_mat_select.getText(),extwall_mat_choices,mRBCA_extwall_mat_select);
        } else if (id == R.id.rbca_extfeat_type_select){
            showSelectionDialog((String) mRBCA_extfeat_type_select.getText(),extfeat_type_choices,mRBCA_extfeat_type_select);
        } else if (id == R.id.rbca_win_type_select){
            showSelectionDialog((String) mRBCA_win_type_select.getText(),win_type_choices,mRBCA_win_type_select);
        } else if (id == R.id.rbca_win_mat_select){
            showSelectionDialog((String) mRBCA_win_mat_select.getText(),win_mat_choices,mRBCA_win_mat_select);
        } else if (id == R.id.rbca_roof_type_select){
            showSelectionDialog((String) mRBCA_roof_type_select.getText(),roof_type_choices,mRBCA_roof_type_select);
        } else if (id == R.id.rbca_roof_mat_select){
            showSelectionDialog((String) mRBCA_roof_mat_select.getText(),roof_mat_choices,mRBCA_roof_mat_select);
        } else if (id == R.id.rbca_int_cond_select){
            showSelectionDialog((String) mRBCA_int_cond_select.getText(),int_cond_choices,mRBCA_int_cond_select);
        } else if (id == R.id.rbca_int_collect_type_select){
            showSelectionDialog((String) mRBCA_int_collect_type_select.getText(),int_collect_type_choices,mRBCA_int_collect_type_select);
        } else if (id == R.id.rbca_landveg_feat_select){
            showSelectionDialog((String) mRBCA_landveg_feat_select.getText(),landveg_feat_choices,mRBCA_landveg_feat_select);
        } else if (id == R.id.rbca_landblt_feat_select){
            showSelectionDialog((String) mRBCA_landblt_feat_select.getText(),landblt_feat_choices,mRBCA_landblt_feat_select);
        } else if (id == R.id.rbca_hzrd_type_select){
            showSelectionDialog((String) mRBCA_hzrd_type_select.getText(),hzrd_type_choices,mRBCA_hzrd_type_select);
        } else if (id == R.id.rbca_hzrd_hazmat_select){
            showSelectionDialog((String) mRBCA_hzrd_hazmat_select.getText(),hzrd_hazmat_choices,mRBCA_hzrd_hazmat_select);
        } else if (id == R.id.rbca_actn_select){
            showSelectionDialog((String) mRBCA_actn_select.getText(),actn_choices,mRBCA_actn_select);
        } else if (id == R.id.rbca_eval_select){
            showSelectionDialog((String) mRBCA_eval_select.getText(),eval_choices,mRBCA_eval_select);
        }
            
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        
        if (mFormatBar.getVisibility() != View.VISIBLE)
            showFormatBar();
        
        float pos = event.getY();

        if (event.getAction() == 0)
            mLastYPos = pos;

        if (event.getAction() > 1) {
            if (((mLastYPos - pos) > 2.0f) || ((pos - mLastYPos) > 2.0f))
                mScrollDetected = true;
        }

        mLastYPos = pos;

        if (event.getAction() == 1 && !mScrollDetected) {
            Layout layout = ((TextView) v).getLayout();
            int x = (int) event.getX();
            int y = (int) event.getY();

            x += v.getScrollX();
            y += v.getScrollY();
            if (layout != null) {
                int line = layout.getLineForVertical(y);
                int charPosition = layout.getOffsetForHorizontal(line, x);

                final Spannable s = mContentEditText.getText();
                // check if image span was tapped
                WPImageSpan[] image_spans = s.getSpans(charPosition, charPosition, WPImageSpan.class);

                if (image_spans.length != 0) {
                    final WPImageSpan span = image_spans[0];
                    if (!span.isVideo()) {
                        LayoutInflater factory = LayoutInflater.from(EditPostActivity.this);
                        final View alertView = factory.inflate(R.layout.alert_image_options, null);
                        final TextView imageWidthText = (TextView) alertView.findViewById(R.id.imageWidthText);
                        final EditText titleText = (EditText) alertView.findViewById(R.id.title);
                        // final EditText descText = (EditText)
                        // alertView.findViewById(R.id.description);
                        final EditText caption = (EditText) alertView.findViewById(R.id.caption);
                        final CheckBox featuredCheckBox = (CheckBox) alertView.findViewById(R.id.featuredImage);
                        final CheckBox featuredInPostCheckBox = (CheckBox) alertView.findViewById(R.id.featuredInPost);

                        // show featured image checkboxes if theme support it
                        if (WordPress.currentBlog.isFeaturedImageCapable()) {
                            featuredCheckBox.setVisibility(View.VISIBLE);
                            featuredInPostCheckBox.setVisibility(View.VISIBLE);
                        }

                        featuredCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    featuredInPostCheckBox.setVisibility(View.VISIBLE);
                                } else {
                                    featuredInPostCheckBox.setVisibility(View.GONE);
                                }

                            }
                        });

                        final SeekBar seekBar = (SeekBar) alertView.findViewById(R.id.imageWidth);
                        final Spinner alignmentSpinner = (Spinner) alertView.findViewById(R.id.alignment_spinner);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditPostActivity.this, R.array.alignment_array,
                                android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        alignmentSpinner.setAdapter(adapter);

                        imageWidthText.setText(String.valueOf(span.getWidth()) + "px");
                        seekBar.setProgress(span.getWidth());
                        titleText.setText(span.getTitle());
                        // descText.setText(span.getDescription());
                        caption.setText(span.getCaption());
                        featuredCheckBox.setChecked(span.isFeatured());

                        if (span.isFeatured())
                            featuredInPostCheckBox.setVisibility(View.VISIBLE);
                        else
                            featuredInPostCheckBox.setVisibility(View.GONE);

                        featuredInPostCheckBox.setChecked(span.isFeaturedInPost());

                        alignmentSpinner.setSelection(span.getHorizontalAlignment(), true);

                        seekBar.setMax(100);
                        if (span.getWidth() != 0)
                            seekBar.setProgress(span.getWidth() / 10);
                        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (progress == 0)
                                    progress = 1;
                                imageWidthText.setText(progress * 10 + "px");
                            }
                        });

                        AlertDialog ad = new AlertDialog.Builder(EditPostActivity.this).setTitle(getString(R.string.image_settings))
                                .setView(alertView).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        span.setTitle(titleText.getText().toString());
                                        // span.setDescription(descText.getText().toString());
                                        span.setHorizontalAlignment(alignmentSpinner.getSelectedItemPosition());
                                        span.setWidth(seekBar.getProgress() * 10);
                                        span.setCaption(caption.getText().toString());
                                        span.setFeatured(featuredCheckBox.isChecked());
                                        if (featuredCheckBox.isChecked()) {
                                            // remove featured flag from all
                                            // other images
                                            WPImageSpan[] click_spans = s.getSpans(0, s.length(), WPImageSpan.class);
                                            if (click_spans.length > 1) {
                                                for (int i = 0; i < click_spans.length; i++) {
                                                    WPImageSpan verifySpan = click_spans[i];
                                                    if (verifySpan != span) {
                                                        verifySpan.setFeatured(false);
                                                        verifySpan.setFeaturedInPost(false);
                                                    }
                                                }
                                            }
                                        }
                                        span.setFeaturedInPost(featuredInPostCheckBox.isChecked());
                                    }
                                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                }).create();
                        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        ad.show();
                        mScrollDetected = false;
                        return true;
                    }

                } else {
                    mContentEditText.setMovementMethod(ArrowKeyMovementMethod.getInstance());
                    mContentEditText.setSelection(mContentEditText.getSelectionStart());
                }
            }
        } else if (event.getAction() == 1) {
            mScrollDetected = false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        showCancelAlert(false);
    }

    private void showCancelAlert(final boolean isUpPress) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditPostActivity.this);
        dialogBuilder.setTitle(getResources().getText(R.string.cancel_edit));
        dialogBuilder.setMessage(getResources().getText((mIsPage) ? R.string.sure_to_cancel_edit_page : R.string.sure_to_cancel_edit));
        dialogBuilder.setPositiveButton(getResources().getText(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mIsNewDraft)
                    mPost.delete();
                if (isUpPress && mIsExternalInstance) {
                    Intent intent = new Intent(EditPostActivity.this, (mIsPage) ? PagesActivity.class : PostsActivity.class);
                    if (mIsPage)
                        intent.putExtra("viewPages", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("returnStatus", "CANCEL");
                    Intent mIntent = new Intent();
                    mIntent.putExtras(bundle);
                    setResult(RESULT_OK, mIntent);
                }
                finish();
            }
        });
        dialogBuilder.setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setCancelable(true);
        dialogBuilder.create().show();
    }

    private void initBlog() {
        if (WordPress.getCurrentBlog() == null)
            showBlogErrorAndFinish();
    }

    private void getLocationProvider() {
        boolean hasLocationProvider = false;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        for (String providerName : providers) {
            if (providerName.equals(LocationManager.GPS_PROVIDER) || providerName.equals(LocationManager.NETWORK_PROVIDER)) {
                hasLocationProvider = true;
            }
        }
        if (hasLocationProvider && mBlog.isLocation() && !mIsPage) {
            enableLBSButtons();
        }
    }

    private boolean selectBlogForShareAction() {

        mIsNew = true;
        mLocalDraft = true;
        
        List<Map<String, Object>> accounts = WordPress.wpDB.getAccounts();

        if (accounts.size() > 0) {

            final String blogNames[] = new String[accounts.size()];
            final int accountIDs[] = new int[accounts.size()];

            for (int i = 0; i < accounts.size(); i++) {

                Map<String, Object> curHash = accounts.get(i);
                try {
                    blogNames[i] = EscapeUtils.unescapeHtml(curHash.get("blogName").toString());
                } catch (Exception e) {
                    blogNames[i] = curHash.get("url").toString();
                }
                accountIDs[i] = (Integer) curHash.get("id");
                try {
                    mBlog = new Blog(accountIDs[i]);
                } catch (Exception e) {
                    showBlogErrorAndFinish();
                    return false;
                }
            }

            // Don't prompt if they have one blog only
            if (accounts.size() > 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPostActivity.this);
                builder.setCancelable(false);
                builder.setTitle(getResources().getText(R.string.select_a_blog));
                builder.setItems(blogNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        mBlogID = accountIDs[item];
                        try {
                            mBlog = new Blog(mBlogID);
                        } catch (Exception e) {
                            showBlogErrorAndFinish();
                        }
                        WordPress.currentBlog = mBlog;
                        WordPress.wpDB.updateLastBlogId(WordPress.currentBlog.getId());
                        mAccountName = blogNames[item];
                        setTitle(EscapeUtils.unescapeHtml(mAccountName) + " - "
                                + getResources().getText((mIsPage) ? R.string.new_page : R.string.new_post));
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                mBlogID = accountIDs[0];
                try {
                    mBlog = new Blog(mBlogID);
                } catch (Exception e) {
                    showBlogErrorAndFinish();
                    return false;
                }
                WordPress.currentBlog = mBlog;
                WordPress.wpDB.updateLastBlogId(WordPress.currentBlog.getId());
                mAccountName = blogNames[0];
                setTitle(EscapeUtils.unescapeHtml(mAccountName) + " - "
                        + getResources().getText((mIsPage) ? R.string.new_page : R.string.new_post));
            };
            return true;
        } else {
            // no account, load main view to load new account view
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.no_account), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, NewAccountActivity.class));
            finish();
            return false;
        }
    }

    private void showBlogErrorAndFinish() {
        Toast.makeText(this, getResources().getText(R.string.blog_not_found), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void formatBtnClick(ToggleButton toggleButton, String tag) {
        try {
            Spannable s = mContentEditText.getText();
            int selectionStart = mContentEditText.getSelectionStart();
            mStyleStart = selectionStart;
            int selectionEnd = mContentEditText.getSelectionEnd();

            if (selectionStart > selectionEnd) {
                int temp = selectionEnd;
                selectionEnd = selectionStart;
                selectionStart = temp;
            }

            if (mLocalDraft) {
                if (selectionEnd > selectionStart) {
                    Spannable str = mContentEditText.getText();
                    if (tag.equals("strong")) {
                        StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

                        boolean exists = false;
                        for (int i = 0; i < ss.length; i++) {
                            int style = ((StyleSpan) ss[i]).getStyle();
                            if (style == android.graphics.Typeface.BOLD) {
                                str.removeSpan(ss[i]);
                                exists = true;
                            }
                        }

                        if (!exists) {
                            str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), selectionStart, selectionEnd,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        toggleButton.setChecked(false);
                    } else if (tag.equals("em")) {
                        StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

                        boolean exists = false;
                        for (int i = 0; i < ss.length; i++) {
                            int style = ((StyleSpan) ss[i]).getStyle();
                            if (style == android.graphics.Typeface.ITALIC) {
                                str.removeSpan(ss[i]);
                                exists = true;
                            }
                        }

                        if (!exists) {
                            str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), selectionStart, selectionEnd,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        toggleButton.setChecked(false);
                    } else if (tag.equals("u")) {

                        WPUnderlineSpan[] ss = str.getSpans(selectionStart, selectionEnd, WPUnderlineSpan.class);

                        boolean exists = false;
                        for (int i = 0; i < ss.length; i++) {
                            str.removeSpan(ss[i]);
                            exists = true;
                        }

                        if (!exists) {
                            str.setSpan(new WPUnderlineSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        toggleButton.setChecked(false);
                    } else if (tag.equals("strike")) {

                        StrikethroughSpan[] ss = str.getSpans(selectionStart, selectionEnd, StrikethroughSpan.class);

                        boolean exists = false;
                        for (int i = 0; i < ss.length; i++) {
                            str.removeSpan(ss[i]);
                            exists = true;
                        }

                        if (!exists) {
                            str.setSpan(new StrikethroughSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        toggleButton.setChecked(false);
                    } else if (tag.equals("blockquote")) {

                        QuoteSpan[] ss = str.getSpans(selectionStart, selectionEnd, QuoteSpan.class);

                        boolean exists = false;
                        for (int i = 0; i < ss.length; i++) {
                            str.removeSpan(ss[i]);
                            exists = true;
                        }

                        if (!exists) {
                            str.setSpan(new QuoteSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        toggleButton.setChecked(false);
                    }
                } else if (!toggleButton.isChecked()) {

                    if (tag.equals("strong") || tag.equals("em")) {

                        StyleSpan[] ss = s.getSpans(mStyleStart - 1, mStyleStart, StyleSpan.class);

                        for (int i = 0; i < ss.length; i++) {
                            int tagStart = s.getSpanStart(ss[i]);
                            int tagEnd = s.getSpanEnd(ss[i]);
                            if (ss[i].getStyle() == android.graphics.Typeface.BOLD && tag.equals("strong")) {
                                tagStart = s.getSpanStart(ss[i]);
                                tagEnd = s.getSpanEnd(ss[i]);
                                s.removeSpan(ss[i]);
                                s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), tagStart, tagEnd,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if (ss[i].getStyle() == android.graphics.Typeface.ITALIC && tag.equals("em")) {
                                tagStart = s.getSpanStart(ss[i]);
                                tagEnd = s.getSpanEnd(ss[i]);
                                s.removeSpan(ss[i]);
                                s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), tagStart, tagEnd,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    } else if (tag.equals("u")) {
                        WPUnderlineSpan[] us = s.getSpans(mStyleStart - 1, mStyleStart, WPUnderlineSpan.class);
                        for (int i = 0; i < us.length; i++) {
                            int tagStart = s.getSpanStart(us[i]);
                            int tagEnd = s.getSpanEnd(us[i]);
                            s.removeSpan(us[i]);
                            s.setSpan(new WPUnderlineSpan(), tagStart, tagEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else if (tag.equals("strike")) {
                        StrikethroughSpan[] ss = s.getSpans(mStyleStart - 1, mStyleStart, StrikethroughSpan.class);
                        for (int i = 0; i < ss.length; i++) {
                            int tagStart = s.getSpanStart(ss[i]);
                            int tagEnd = s.getSpanEnd(ss[i]);
                            s.removeSpan(ss[i]);
                            s.setSpan(new StrikethroughSpan(), tagStart, tagEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else if (tag.equals("blockquote")) {
                        QuoteSpan[] ss = s.getSpans(mStyleStart - 1, mStyleStart, QuoteSpan.class);
                        for (int i = 0; i < ss.length; i++) {
                            int tagStart = s.getSpanStart(ss[i]);
                            int tagEnd = s.getSpanEnd(ss[i]);
                            s.removeSpan(ss[i]);
                            s.setSpan(new QuoteSpan(), tagStart, tagEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            } else {
                String startTag = "<" + tag + ">";
                String endTag = "</" + tag + ">";
                Editable content = mContentEditText.getText();
                if (selectionEnd > selectionStart) {
                    content.insert(selectionStart, startTag);
                    content.insert(selectionEnd + startTag.length(), endTag);
                    toggleButton.setChecked(false);
                    mContentEditText.setSelection(selectionEnd + startTag.length() + endTag.length());
                } else if (toggleButton.isChecked()) {
                    content.insert(selectionStart, startTag);
                    mContentEditText.setSelection(selectionEnd + startTag.length());
                } else if (!toggleButton.isChecked()) {
                    content.insert(selectionEnd, endTag);
                    mContentEditText.setSelection(selectionEnd + endTag.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchPictureLibrary() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mCurrentActivityRequest = ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY;
        startActivityForResult(photoPickerIntent, ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY);
    }

    private void launchCamera() {
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditPostActivity.this);
            dialogBuilder.setTitle(getResources().getText(R.string.sdcard_title));
            dialogBuilder.setMessage(getResources().getText(R.string.sdcard_message));
            dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            dialogBuilder.setCancelable(true);
            dialogBuilder.create().show();
        } else {
            String dcimFolderName = Environment.DIRECTORY_DCIM;
            if (dcimFolderName == null)
                dcimFolderName = "DCIM";
            mMediaCapturePath = Environment.getExternalStorageDirectory() + File.separator + dcimFolderName + File.separator + "Camera"
                    + File.separator + "wp-" + System.currentTimeMillis() + ".jpg";
            Intent takePictureFromCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureFromCameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mMediaCapturePath)));

            // make sure the directory we plan to store the recording in exists
            File directory = new File(mMediaCapturePath).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                try {
                    throw new IOException("Path to file could not be created.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCurrentActivityRequest = ACTIVITY_REQUEST_CODE_TAKE_PHOTO;
            startActivityForResult(takePictureFromCameraIntent, ACTIVITY_REQUEST_CODE_TAKE_PHOTO);
        }
    }

    private void launchVideoLibrary() {
        Intent videoPickerIntent = new Intent(Intent.ACTION_PICK);
        videoPickerIntent.setType("video/*");
        mCurrentActivityRequest = ACTIVITY_REQUEST_CODE_VIDEO_LIBRARY;
        startActivityForResult(videoPickerIntent, ACTIVITY_REQUEST_CODE_VIDEO_LIBRARY);
    }

    private void launchVideoCamera() {
        mCurrentActivityRequest = ACTIVITY_REQUEST_CODE_TAKE_VIDEO;
        startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE), ACTIVITY_REQUEST_CODE_TAKE_VIDEO);
    }

    private LocationResult locationResult = new LocationResult() {
        @Override
        public void gotLocation(Location location) {
            if (location != null) {
                mCurrentLocation = location;
                new getAddressTask().execute(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mLocationText.setText(getString(R.string.location_not_found));
                    }
                });
            }
        }
    };

    private void enableLBSButtons() {
        mLocationHelper = new LocationHelper();
        ((RelativeLayout) findViewById(R.id.section3)).setVisibility(View.VISIBLE);
        Button viewMap = (Button) findViewById(R.id.viewMap);
        Button updateLocation = (Button) findViewById(R.id.updateLocation);
        Button removeLocation = (Button) findViewById(R.id.removeLocation);
        updateLocation.setOnClickListener(this);
        removeLocation.setOnClickListener(this);
        viewMap.setOnClickListener(this);
        if (mIsNew)
            mLocationHelper.getLocation(EditPostActivity.this, locationResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            if (mQuickMediaType >= 0) {
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }
            if (mFormatBar.getVisibility() == View.VISIBLE)
                hideFormatBar();
            return;
        }

        if (data != null || ((requestCode == ACTIVITY_REQUEST_CODE_TAKE_PHOTO || requestCode == ACTIVITY_REQUEST_CODE_TAKE_VIDEO))) {
            Bundle extras;

            switch (requestCode) {
            case ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY:
                Uri imageUri = data.getData();
                String imgPath = imageUri.toString();
                addMedia(imgPath, imageUri);
                break;
            case ACTIVITY_REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        File f = new File(mMediaCapturePath);
                        Uri capturedImageUri = Uri.fromFile(f);
                        f = null;
                        addMedia(capturedImageUri.toString(), capturedImageUri);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ACTIVITY_REQUEST_CODE_VIDEO_LIBRARY:
                Uri videoUri = data.getData();
                String videoPath = videoUri.toString();
                addMedia(videoPath, videoUri);
                break;
            case ACTIVITY_REQUEST_CODE_TAKE_VIDEO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri capturedVideo = data.getData();
                    addMedia(capturedVideo.toString(), capturedVideo);
                }
                break;
            case ACTIVITY_REQUEST_CODE_CREATE_LINK:
                try {
                    extras = data.getExtras();
                    String linkURL = extras.getString("linkURL");
                    if (!linkURL.equals("http://") && !linkURL.equals("")) {

                        if (mSelectionStart > mSelectionEnd) {
                            int temp = mSelectionEnd;
                            mSelectionEnd = mSelectionStart;
                            mSelectionStart = temp;
                        }
                        Editable str = mContentEditText.getText();
                        if (mLocalDraft) {
                            if (extras.getString("linkText") == null) {
                                if (mSelectionStart < mSelectionEnd)
                                    str.delete(mSelectionStart, mSelectionEnd);
                                str.insert(mSelectionStart, linkURL);
                                str.setSpan(new URLSpan(linkURL), mSelectionStart, mSelectionStart + linkURL.length(),
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mContentEditText.setSelection(mSelectionStart + linkURL.length());
                            } else {
                                String linkText = extras.getString("linkText");
                                if (mSelectionStart < mSelectionEnd)
                                    str.delete(mSelectionStart, mSelectionEnd);
                                str.insert(mSelectionStart, linkText);
                                str.setSpan(new URLSpan(linkURL), mSelectionStart, mSelectionStart + linkText.length(),
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mContentEditText.setSelection(mSelectionStart + linkText.length());
                            }
                        } else {
                            if (extras.getString("linkText") == null) {
                                if (mSelectionStart < mSelectionEnd)
                                    str.delete(mSelectionStart, mSelectionEnd);
                                String urlHTML = "<a href=\"" + linkURL + "\">" + linkURL + "</a>";
                                str.insert(mSelectionStart, urlHTML);
                                mContentEditText.setSelection(mSelectionStart + urlHTML.length());
                            } else {
                                String linkText = extras.getString("linkText");
                                if (mSelectionStart < mSelectionEnd)
                                    str.delete(mSelectionStart, mSelectionEnd);
                                String urlHTML = "<a href=\"" + linkURL + "\">" + linkText + "</a>";
                                str.insert(mSelectionStart, urlHTML);
                                mContentEditText.setSelection(mSelectionStart + urlHTML.length());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACTIVITY_REQUEST_CODE_SELECT_CATEGORIES:
                extras = data.getExtras();
                String cats = extras.getString("selectedCategories");
                String[] splitCats = cats.split(",");
                if (splitCats.length < 1)
                    return;
                mCategories = new JSONArray();
                for (int i = 0; i < splitCats.length; i++) {
                    mCategories.put(splitCats[i]);
                }
                mCategoriesText.setVisibility(View.VISIBLE);
                mCategoriesText.setText(getString(R.string.selected_categories) + " " + getCategoriesCSV());
                break;
            }
        }// end null check
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
        case ID_DIALOG_DATE:
            DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            dpd.setTitle("");
            return dpd;
        case ID_DIALOG_TIME:
            TimePickerDialog tpd = new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
            tpd.setTitle("");
            return tpd;
        case ID_DIALOG_LOADING:
            ProgressDialog loadingDialog = new ProgressDialog(this);
            loadingDialog.setMessage(getResources().getText(R.string.loading));
            loadingDialog.setIndeterminate(true);
            loadingDialog.setCancelable(true);
            return loadingDialog;
        }
        return super.onCreateDialog(id);
    }

    private boolean savePost(boolean autoSave) {

        String title = mTitleEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String pubDate = mPubDateText.getText().toString();
        String content = "";
        
        

        if (mLocalDraft || mIsNew && !autoSave) {
            Editable e = mContentEditText.getText();
            if (android.os.Build.VERSION.SDK_INT >= 14) {
                // remove suggestion spans, they cause craziness in
                // WPHtml.toHTML().
                CharacterStyle[] style = e.getSpans(0, e.length(), CharacterStyle.class);
                for (int i = 0; i < style.length; i++) {
                    if (style[i].getClass().getName().equals("android.text.style.SuggestionSpan"))
                        e.removeSpan(style[i]);
                }
            }
            content = EscapeUtils.unescapeHtml(WPHtml.toHtml(e));
            // replace duplicate <p> tags so there's not duplicates, trac #86
            content = content.replace("<p><p>", "<p>");
            content = content.replace("</p></p>", "</p>");
            content = content.replace("<br><br>", "<br>");
            // sometimes the editor creates extra tags
            content = content.replace("</strong><strong>", "").replace("</em><em>", "").replace("</u><u>", "")
                    .replace("</strike><strike>", "").replace("</blockquote><blockquote>", "");
        } else {
            content = mContentEditText.getText().toString();
        }

        long pubDateTimestamp = 0;
        if (!pubDate.equals(getResources().getText(R.string.immediately))) {
            if (mIsCustomPubDate)
                pubDateTimestamp = mCustomPubDate;
            else if (!mIsNew)
                pubDateTimestamp = mPost.getDate_created_gmt();
        }

        String tags = "", postFormat = "";
        if (!mIsPage) {
            tags = mTagsEditText.getText().toString();
            // post format
            Spinner postFormatSpinner = (Spinner) findViewById(R.id.postFormat);
            postFormat = mPostFormats[postFormatSpinner.getSelectedItemPosition()];
        }

        String images = "";
        boolean success = false;

        
        //validates if content is empty
        if (content.equals("") && !autoSave) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditPostActivity.this);
            dialogBuilder.setTitle(getResources().getText(R.string.empty_fields));
            dialogBuilder.setMessage(getResources().getText(R.string.title_post_required));
            dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            dialogBuilder.setCancelable(true);
            dialogBuilder.create().show();
        } else {

            if (!mIsNew) {
                // update the images
                mPost.deleteMediaFiles();
                Editable s = mContentEditText.getText();
                WPImageSpan[] click_spans = s.getSpans(0, s.length(), WPImageSpan.class);

                if (click_spans.length != 0) {

                    for (int i = 0; i < click_spans.length; i++) {
                        WPImageSpan wpIS = click_spans[i];
                        images += wpIS.getImageSource().toString() + ",";

                        MediaFile mf = new MediaFile();
                        mf.setPostID(mPost.getId());
                        mf.setTitle(wpIS.getTitle());
                        mf.setCaption(wpIS.getCaption());
                        mf.setDescription(wpIS.getDescription());
                        mf.setFeatured(wpIS.isFeatured());
                        mf.setFeaturedInPost(wpIS.isFeaturedInPost());
                        mf.setFileName(wpIS.getImageSource().toString());
                        mf.setHorizontalAlignment(wpIS.getHorizontalAlignment());
                        mf.setWidth(wpIS.getWidth());
                        mf.save();

                        int tagStart = s.getSpanStart(wpIS);
                        if (!autoSave) {
                            s.removeSpan(wpIS);
                            s.insert(tagStart, "<img android-uri=\"" + wpIS.getImageSource().toString() + "\" />");
                            if (mLocalDraft)
                                content = EscapeUtils.unescapeHtml(WPHtml.toHtml(s));
                            else
                                content = s.toString();
                        }
                    }
                }
            }

            final String moreTag = "<!--more-->";
            
            //////////////////////SURVEYS FOR ANDROID
            
            int selected_occucy = mRBCA_bldg_occucy_spinner.getSelectedItemPosition();
            String occupancy = "";
            switch (selected_occucy) {
            case 0:
                occupancy = "Occupied";
                break;
            case 1:
                occupancy = "Vacant";
                break;
            case 2:
                occupancy = "Unknown";
                break;
            }
            
            int selected_coord_corner = mRBCA_coord_corner_spinner.getSelectedItemPosition();
            String coord_corner = "";
            switch (selected_coord_corner) {
            case 0:
                coord_corner = "N";
                break;
            case 1:
                coord_corner = "NE";
                break;
            case 2:
                coord_corner = "E";
                break;
            case 3:
                coord_corner = "SE";
                break;
            case 4:
                coord_corner = "S";
                break;
            case 5:
                coord_corner = "SW";
                break;
            case 6:
                coord_corner = "W";
                break;
            case 7:
                coord_corner = "NW";
                break;
            } 
            
            String coord_loc_oth = mRBCA_coord_loc_oth.getText().toString();
            String coord_notes = mRBCA_coord_notes.getText().toString();
            String addr_no = mRBCA_addr_no.getText().toString();
            String addr_street = mRBCA_addr_street.getText().toString();
            String bldg_area = mRBCA_bldg_area_select.getText().toString();
            String bldg_posting = mRBCA_bldg_posting_select.getText().toString();
            String bldg_posting_oth = mRBCA_bldg_posting_oth.getText().toString();
            
            
            int selected_coord_loc = mRBCA_coord_loc_spinner.getSelectedItemPosition();
            String coord_location = "";

            switch (selected_coord_loc) {
            case 0:
                coord_location = "Entrance";
                break;
            case 1:
                coord_location = "Corner";
                break;
            case 2:
                coord_location = "Other";
                break;
            }
            
            String occucyAvailableSelection = mRBCA_bldg_occucy_avail.getText().toString(); 
            int occucy_available = 0;
            if (occucyAvailableSelection.equals("Yes"))
                    occucy_available = 1;
            
            double bldg_stories =0.0;
            if (!mRBCA_bldg_stories.getText().toString().equals("")){
                bldg_stories = Double.parseDouble(mRBCA_bldg_stories.getText().toString());
            }
            
            double bldg_width = 0.0;
            if (!mRBCA_bldg_width.getText().toString().equals("")){
                bldg_width = Double.parseDouble(mRBCA_bldg_width.getText().toString());
            }
            
            double bldg_length = 0.0;
            if (!mRBCA_bldg_length.getText().toString().equals("")){
                bldg_length = Double.parseDouble(mRBCA_bldg_length.getText().toString());
            }
            
            String uses = mRBCA_bldg_use_select.getText().toString();
            String uses_oth = mRBCA_bldg_use_oth.getText().toString();
            
            String outbldgSelection = mRBCA_bldg_outbldg.getText().toString(); 
            int outbldg = 0;
            if (outbldgSelection.equals("Yes"))
                    outbldg = 1;
            
            String outbldg_notes = mRBCA_bldg_outbldg_notes.getText().toString();
            
            int units_res = 0;
            if (!mRBCA_bldg_units_res.getText().toString().equals("")){
                units_res = Integer.parseInt(mRBCA_bldg_units_res.getText().toString());
            } 
            
            
            
            int units_comm = 0;
            if (!mRBCA_bldg_units_comm.getText().toString().equals("")){
                units_comm = Integer.parseInt(mRBCA_bldg_units_comm.getText().toString());
            }
            
            
            String occu_name = mRBCA_bldg_occu_name.getText().toString();
            
            int occu_phone = 0;
            if (!mRBCA_bldg_occu_phone.getText().toString().equals("")){
                occu_phone = Integer.parseInt(mRBCA_bldg_occu_phone.getText().toString());
            }
            
            
            String occu_notes = mRBCA_bldg_notes.getText().toString();
            
            String hist_desig = mRBCA_hist_desig_select.getText().toString();
            
            String hist_desig_oth = mRBCA_hist_desig_oth.getText().toString();
            
            int selected_hist_dist = mRBCA_hist_dist_spinner.getSelectedItemPosition();
            String hist_dist = "";

            switch (selected_hist_dist) {
            case 0:
                hist_dist = "Yes";
                break;
            case 1:
                hist_dist = "No";
                break;
            case 2:
                hist_dist = "Unknown";
                break;
            }
            
            String hist_dist_name = mRBCA_hist_dist_name.getText().toString();
            
            String histAppearSelection = mRBCA_hist_appear.getText().toString(); 
            int hist_appear = 0;
            if (histAppearSelection.equals("Yes"))
                    hist_appear = 1;
            
            int selected_hist_age = mRBCA_hist_age_spinner.getSelectedItemPosition();
            int hist_age = 0;

            switch (selected_hist_age) {
            case 0:
                hist_age = 1;
                break;
            case 1:
                hist_age = 2;
                break;
            case 2:
                hist_age = 3;
                break;
            }
            
            int selected_hist_age_meta = mRBCA_hist_age_meta_spinner.getSelectedItemPosition();
            String hist_age_meta = "";

            switch (selected_hist_age_meta) {
            case 0:
                hist_age_meta = "Estimated";
                break;
            case 1:
                hist_age_meta = "Verified";
                break;
            case 2:
                hist_age_meta = "Reported";
                break;
            }
           
            
            int hist_yr_built = 0;
            if (!mRBCA_hist_yr_built.getText().toString().equals("")){
                hist_yr_built = Integer.parseInt(mRBCA_hist_yr_built.getText().toString());
            } 
            
            int hist_age_src = mRBCA_hist_age_src_spinner.getSelectedItemPosition();
            
            String hist_age_src_oth = mRBCA_hist_age_src_oth.getText().toString();
            
            String hist_notes = mRBCA_hist_notes.getText().toString();
           
            String dmg_source = mRBCA_dmg_source_select.getText().toString();
            
            String dmg_source_oth = mRBCA_dmg_source_oth.getText().toString();
            
            int dmg_total = mRBCA_dmg_total_spinner.getSelectedItemPosition();
            
            String dmg_desc = mRBCA_dmg_desc.getText().toString();
            
            int selected_struct_type = mRBCA_struct_type_spinner.getSelectedItemPosition();
            String struct_type = "";
            switch (selected_struct_type) {
            case 0:
                struct_type = "Wood Frame";
                break;
            case 1:
                struct_type = "Metal Frame";
                break;
            case 2:
                struct_type = "Masonry";
                break;
            case 3:
                struct_type = "Manufactured";
                break;
            case 4:
                struct_type = "Other";
                break;
            } 
            
            String struct_type_oth = mRBCA_struct_type_oth.getText().toString();
            
            String struct_defects = mRBCA_struct_defects_select.getText().toString();
            
            int struct = mRBCA_struct_spinner.getSelectedItemPosition();
            
            String struct_notes = mRBCA_struct_notes.getText().toString();
            
            int selected_found_type = mRBCA_found_type_spinner.getSelectedItemPosition();
            String found_type = "";
            switch (selected_found_type) {
            case 0:
                found_type = "Slab";
                break;
            case 1:
                found_type = "Basement";
                break;
            case 2:
                found_type = "Stem/Chain Wall";
                break;
            case 3:
                found_type = "Raised Pier/Post";
                break;
            case 4:
                found_type = "Other";
                break;
            } 
            
            String found_type_oth = mRBCA_found_type_oth.getText().toString();
            
            int found = mRBCA_found_spinner.getSelectedItemPosition();
            
            String found_notes = mRBCA_found_notes.getText().toString();
            
            String extwall_mat = mRBCA_extwall_mat_select.getText().toString();
            
            String extwall_mat_oth = mRBCA_extwall_mat_oth.getText().toString();
            
            int extwall = mRBCA_extwall_spinner.getSelectedItemPosition();
            
            String extwall_notes = mRBCA_extwall_notes.getText().toString();
            
            String extfeat_type = mRBCA_extfeat_type_select.getText().toString();
            
            String extfeat_type_oth = mRBCA_extfeat_type_oth.getText().toString();
            
            int extfeat = mRBCA_extfeat_spinner.getSelectedItemPosition();
            
            String extfeat_notes = mRBCA_extfeat_notes.getText().toString();
            
            String win_type = mRBCA_win_type_select.getText().toString();
            
            String win_type_oth = mRBCA_win_type_oth.getText().toString();
            
            String win_mat = mRBCA_win_mat_select.getText().toString();
            
            String win_mat_oth = mRBCA_win_mat_oth.getText().toString();
            
            int win = mRBCA_win_spinner.getSelectedItemPosition();
            
            String win_notes = mRBCA_win_notes.getText().toString();
            
            String roof_type = mRBCA_roof_type_select.getText().toString();
            
            String roof_type_oth = mRBCA_roof_type_oth.getText().toString();
            
            String roof_mat = mRBCA_roof_mat_select.getText().toString();
            
            String roof_mat_oth = mRBCA_roof_mat_oth.getText().toString();
            
            int roof = mRBCA_roof_spinner.getSelectedItemPosition();
            
            String roof_notes = mRBCA_roof_notes.getText().toString();
            
            String int_cond = mRBCA_int_cond_select.getText().toString();
            
            int int_collect_extant = mRBCA_int_collect_extant_spinner.getSelectedItemPosition();
            
            String int_collect_type = mRBCA_int_collect_type_select.getText().toString();
            
            String int_collect_type_oth = mRBCA_int_collect_type_oth.getText().toString();
            
            String int_notes = mRBCA_int_notes.getText().toString();
            
            String landveg_feat = mRBCA_landveg_feat_select.getText().toString();
            
            String landveg_feat_oth = mRBCA_landveg_feat_oth.getText().toString();
            
            int landveg = mRBCA_landveg_spinner.getSelectedItemPosition();
            
            String landveg_notes = mRBCA_landveg_notes.getText().toString();
            
            String landblt_feat = mRBCA_landblt_feat_select.getText().toString();
            
            String landblt_feat_oth = mRBCA_landblt_feat_oth.getText().toString();
            
            int landblt = mRBCA_landblt_spinner.getSelectedItemPosition();
            
            String landblt_notes = mRBCA_landblt_notes.getText().toString();
            
            int hzrd = mRBCA_hzrd_spinner.getSelectedItemPosition();
            
            String hzrd_type = mRBCA_hzrd_type_select.getText().toString();
            
            String hzrd_type_oth = mRBCA_hzrd_type_oth.getText().toString();
            
            String hzrd_notes = mRBCA_hzrd_notes.getText().toString();

            String hzrd_hazmat = mRBCA_hzrd_hazmat_select.getText().toString();
            
            String hzrd_hazmat_oth = mRBCA_hzrd_hazmat_oth.getText().toString();
            
            String actn = mRBCA_actn_select.getText().toString();
            
            String actn_oth = mRBCA_actn_oth.getText().toString();
            
            String eval = mRBCA_eval_select.getText().toString();
            
            String eval_oth = mRBCA_eval_oth.getText().toString();


            
            
            ////////end Moca for ANDROID
            
            int selectedStatus = mStatusSpinner.getSelectedItemPosition();
            String status = "";

            switch (selectedStatus) {
            case 0:
                status = "publish";
                break;
            case 1:
                status = "draft";
                break;
            case 2:
                status = "pending";
                break;
            case 3:
                status = "private";
                break;
            case 4:
                status = "localdraft";
                break;
            }

            Double latitude = 0.0;
            Double longitude = 0.0;
            if (mBlog.isLocation()) {

                // attempt to get the device's location
                try {
                    latitude = mCurrentLocation.getLatitude();
                    longitude = mCurrentLocation.getLongitude();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (mIsNew) {
                mPost = new Post(mBlogID, title, content, images, pubDateTimestamp, mCategories.toString(), tags, status, password,
                        latitude, longitude, mIsPage, postFormat, true, false,coord_location,coord_loc_oth,coord_corner, 
                        coord_notes, addr_no, addr_street,bldg_area,bldg_posting,bldg_posting_oth,occupancy,occucy_available,bldg_stories,bldg_width,
                        bldg_length,uses,uses_oth,outbldg, outbldg_notes, units_res, units_comm,occu_name, occu_phone, occu_notes,hist_desig, hist_desig_oth,
                        hist_dist, hist_dist_name,hist_appear,hist_age,hist_age_meta, hist_yr_built, hist_age_src, hist_age_src_oth,hist_notes,dmg_source, dmg_source_oth,dmg_total,dmg_desc,
                        struct_type,struct_type_oth,struct_defects,struct,struct_notes,found_type,found_type_oth,found,found_notes,extwall_mat,extwall_mat_oth,extwall,extwall_notes,extfeat_type,extfeat_type_oth,
                        extfeat,extfeat_notes,win_type,win_type_oth,win_mat,win_mat_oth,win,win_notes,roof_type,roof_type_oth,roof_mat,roof_mat_oth,roof,roof_notes,int_cond,int_collect_extant, 
                        int_collect_type,int_collect_type_oth,int_notes,landveg_feat,landveg_feat_oth,landveg,landveg_notes,landblt_feat,landblt_feat_oth,landblt,landblt_notes,hzrd,hzrd_type,
                        hzrd_type_oth,hzrd_notes,hzrd_hazmat,hzrd_hazmat_oth,actn,actn_oth,eval,eval_oth);
                mPost.setLocalDraft(true);

                // split up the post content if there's a more tag
                if (content.indexOf(moreTag) >= 0) {
                    mPost.setDescription(content.substring(0, content.indexOf(moreTag)));
                    mPost.setMt_text_more(content.substring(content.indexOf(moreTag) + moreTag.length(), content.length()));
                }

                success = mPost.save();

                if (success) {
                    mIsNew = false;
                    mIsNewDraft = true;
                }

                mPost.deleteMediaFiles();

                Spannable s = mContentEditText.getText();
                WPImageSpan[] image_spans = s.getSpans(0, s.length(), WPImageSpan.class);

                if (image_spans.length != 0) {

                    for (int i = 0; i < image_spans.length; i++) {
                        WPImageSpan wpIS = image_spans[i];
                        images += wpIS.getImageSource().toString() + ",";

                        MediaFile mf = new MediaFile();
                        mf.setPostID(mPost.getId());
                        mf.setTitle(wpIS.getTitle());
                        mf.setCaption(wpIS.getCaption());
                        // mf.setDescription(wpIS.getDescription());
                        mf.setFeatured(wpIS.isFeatured());
                        mf.setFeaturedInPost(wpIS.isFeaturedInPost());
                        mf.setFileName(wpIS.getImageSource().toString());
                        mf.setFilePath(wpIS.getImageSource().toString());
                        mf.setHorizontalAlignment(wpIS.getHorizontalAlignment());
                        mf.setWidth(wpIS.getWidth());
                        mf.setVideo(wpIS.isVideo());
                        mf.save();
                    }
                }

                WordPress.currentPost = mPost;

            } else {

                if (mCurrentLocation == null) {
                    latitude = mPost.getLatitude();
                    longitude = mPost.getLongitude();
                }

                mPost.setTitle(title);
                // split up the post content if there's a more tag
                if (mLocalDraft && content.indexOf(moreTag) >= 0) {
                    mPost.setDescription(content.substring(0, content.indexOf(moreTag)));
                    mPost.setMt_text_more(content.substring(content.indexOf(moreTag) + moreTag.length(), content.length()));
                } else {
                    mPost.setDescription(content);
                    mPost.setMt_text_more("");
                }
                mPost.setMediaPaths(images);
                mPost.setDate_created_gmt(pubDateTimestamp);
                mPost.setCategories(mCategories);
                mPost.setMt_keywords(tags);
                mPost.setPost_status(status);
                mPost.setWP_password(password);
                mPost.setLatitude(latitude);
                mPost.setLongitude(longitude);
                
                mPost.setWP_post_form(postFormat);
                if (!mPost.isLocalDraft())
                    mPost.setLocalChange(true);
                
                ///////Surveys for Android
                mPost.setRBCA_coord_loc(coord_location);
                mPost.setRBCA_coord_loc_oth(coord_loc_oth);
                mPost.setRBCA_coord_corner(coord_corner);
                mPost.setRBCA_coord_notes(coord_notes);
                mPost.setRBCA_addr_no(addr_no);
                mPost.setRBCA_addr_street(addr_street);
                mPost.setRBCA_bldg_area(bldg_area);
                mPost.setRBCA_bldg_posting(bldg_posting);
                mPost.setRBCA_bldg_posting_oth(bldg_posting_oth);
                mPost.setRBCA_bldg_occucy(occupancy);
                mPost.setRBCA_bldg_occucy_avail(occucy_available);
                mPost.setRBCA_bldg_stories(bldg_stories);
                mPost.setRBCA_bldg_width(bldg_width);
                mPost.setRBCA_bldg_length(bldg_length);
                mPost.setRBCA_bldg_use(uses);
                mPost.setRBCA_bldg_use_oth(uses_oth);
                mPost.setRBCA_bldg_outbldg(outbldg);
                mPost.setRBCA_bldg_outbldg_notes(outbldg_notes);
                mPost.setRBCA_bldg_units_res(units_res);
                mPost.setRBCA_bldg_units_comm(units_comm);
                mPost.setRBCA_bldg_occu_name(occu_name);
                mPost.setRBCA_bldg_occu_phone(occu_phone);
                mPost.setRBCA_bldg_notes(occu_notes);
                mPost.setRBCA_hist_desig(hist_desig);
                mPost.setRBCA_hist_desig_oth(hist_desig_oth);
                mPost.setRBCA_hist_dist(hist_dist);
                mPost.setRBCA_hist_dist_name(hist_dist_name);
                mPost.setRBCA_hist_appear(hist_appear);
                mPost.setRBCA_hist_age(hist_age);
                mPost.setRBCA_hist_age_meta(hist_age_meta);
                mPost.setRBCA_hist_yr_built(hist_yr_built);
                mPost.setRBCA_hist_age_src(hist_age_src);
                mPost.setRBCA_hist_age_src_oth(hist_age_src_oth);
                mPost.setRBCA_hist_notes(hist_notes);
                mPost.setRBCA_dmg_source(dmg_source);
                mPost.setRBCA_dmg_source_oth(dmg_source_oth);
                mPost.setRBCA_dmg_total(dmg_total);
                mPost.setRBCA_dmg_desc(dmg_desc);
                mPost.setRBCA_struct_type(struct_type);
                mPost.setRBCA_struct_type_oth(struct_type_oth);
                mPost.setRBCA_struct_defects(struct_defects);
                mPost.setRBCA_struct(struct);
                mPost.setRBCA_struct_notes(struct_notes);
                mPost.setRBCA_found_type(found_type);
                mPost.setRBCA_found_type_oth(found_type_oth);
                mPost.setRBCA_found(found);
                mPost.setRBCA_found_notes(found_notes);
                mPost.setRBCA_extwall_mat(extwall_mat);
                mPost.setRBCA_extwall_mat_oth(extwall_mat_oth);
                mPost.setRBCA_extwall(extwall);
                mPost.setRBCA_extwall_notes(extwall_notes);
                mPost.setRBCA_extfeat_type(extfeat_type);
                mPost.setRBCA_extfeat_type_oth(extfeat_type_oth);
                mPost.setRBCA_extfeat(extfeat);
                mPost.setRBCA_extfeat_notes(extfeat_notes);
                mPost.setRBCA_win_type(win_type);
                mPost.setRBCA_win_type_oth(win_type_oth);
                mPost.setRBCA_win_mat(win_mat);
                mPost.setRBCA_win_mat_oth(win_mat_oth);
                mPost.setRBCA_win(win);
                mPost.setRBCA_win_notes(win_notes);
                mPost.setRBCA_roof_type(roof_type);
                mPost.setRBCA_roof_type_oth(roof_type_oth);
                mPost.setRBCA_roof_mat(roof_mat);
                mPost.setRBCA_roof_mat_oth(roof_mat_oth);
                mPost.setRBCA_roof(roof);
                mPost.setRBCA_roof_notes(roof_notes);
                mPost.setRBCA_int_cond(int_cond);
                mPost.setRBCA_int_collect_extant(int_collect_extant);
                mPost.setRBCA_int_collect_type(int_collect_type);
                mPost.setRBCA_int_collect_type_oth(int_collect_type_oth);
                mPost.setRBCA_int_notes(int_notes);
                mPost.setRBCA_landveg_feat(landveg_feat);
                mPost.setRBCA_landveg_feat_oth(landveg_feat_oth);
                mPost.setRBCA_landveg(landveg);
                mPost.setRBCA_landveg_notes(landveg_notes);
                mPost.setRBCA_landblt_feat(landblt_feat);
                mPost.setRBCA_landblt_feat_oth(landblt_feat_oth);
                mPost.setRBCA_landblt(landblt);
                mPost.setRBCA_landblt_notes(landblt_notes);
                mPost.setRBCA_hzrd(hzrd);
                mPost.setRBCA_hzrd_type(hzrd_type);
                mPost.setRBCA_hzrd_type_oth(hzrd_type_oth);
                mPost.setRBCA_hzrd_notes(hzrd_notes);
                mPost.setRBCA_hzrd_hazmat(hzrd_hazmat);
                mPost.setRBCA_hzrd_hazmat_oth(hzrd_hazmat_oth);
                mPost.setRBCA_actn(actn);
                mPost.setRBCA_actn_oth(actn_oth);
                mPost.setRBCA_eval(eval);
                mPost.setRBCA_eval_oth(eval_oth);
                ////END Surveys for Android 
                success = mPost.update();
            }
        }
        return success;
    }

    private class getAddressTask extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... args) {
            Geocoder gcd = new Geocoder(EditPostActivity.this, Locale.getDefault());
            String finalText = "";
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(args[0], args[1], 1);
                String locality = "", adminArea = "", country = "";
                if (addresses.get(0).getLocality() != null)
                    locality = addresses.get(0).getLocality();
                if (addresses.get(0).getAdminArea() != null)
                    adminArea = addresses.get(0).getAdminArea();
                if (addresses.get(0).getCountryName() != null)
                    country = addresses.get(0).getCountryName();

                if (addresses.size() > 0) {
                    finalText = ((locality.equals("")) ? locality : locality + ", ")
                            + ((adminArea.equals("")) ? adminArea : adminArea + " ") + country;
                    if (finalText.equals(""))
                        finalText = getString(R.string.location_not_found);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return finalText;
        }

        protected void onPostExecute(String result) {
            mLocationText.setText(result);
        }
    }

    protected void setContent() {
        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        if (text != null) {

            if (title != null) {
                mTitleEditText.setText(title);
            }

            if (text.contains("youtube_gdata")) {
                // Just use the URL for YouTube links for oEmbed support
                mContentEditText.setText(text);
            } else {
                // add link tag around URLs, trac #64
                text = text.replaceAll("((http|https|ftp|mailto):\\S+)", "<a href=\"$1\">$1</a>");
                mContentEditText.setText(WPHtml.fromHtml(StringHelper.addPTags(text), EditPostActivity.this, mPost));
            }
        } else {
            String action = intent.getAction();
            final String type = intent.getType();
            final ArrayList<Uri> multi_stream;
            if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
                multi_stream = intent.getParcelableArrayListExtra((Intent.EXTRA_STREAM));
            } else {
                multi_stream = new ArrayList<Uri>();
                multi_stream.add((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
            }

            List<Serializable> params = new Vector<Serializable>();
            params.add(multi_stream);
            params.add(type);
            new processAttachmentsTask().execute(params);
        }
    }

    private class processAttachmentsTask extends AsyncTask<List<?>, Void, SpannableStringBuilder> {

        protected void onPreExecute() {
            showDialog(ID_DIALOG_LOADING);
        }

        @Override
        protected SpannableStringBuilder doInBackground(List<?>... args) {
            ArrayList<?> multi_stream = (ArrayList<?>) args[0].get(0);
            String type = (String) args[0].get(1);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            for (int i = 0; i < multi_stream.size(); i++) {
                Uri curStream = (Uri) multi_stream.get(i);
                if (curStream != null && type != null) {
                    String imgPath = curStream.getEncodedPath();
                    ssb = addMediaFromShareAction(imgPath, curStream, ssb);
                }
            }
            return ssb;
        }

        protected void onPostExecute(SpannableStringBuilder result) {
            dismissDialog(ID_DIALOG_LOADING);
            if (result != null) {
                if (result.length() > 0) {
                    mContentEditText.setText(result);
                }
            } else {
                Toast.makeText(EditPostActivity.this, getResources().getText(R.string.gallery_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMedia(String imgPath, Uri curStream) {
        
        if (mFormatBar.getVisibility() == View.VISIBLE)
            hideFormatBar();

        Bitmap resizedBitmap = null;
        ImageHelper ih = new ImageHelper();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        if (width > height)
            width = height;

        Map<String, Object> mediaData = ih.getImageBytesForPath(imgPath, EditPostActivity.this);

        if (mediaData == null) {
            // data stream not returned
            Toast.makeText(EditPostActivity.this, getResources().getText(R.string.gallery_error), Toast.LENGTH_SHORT).show();
            return;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        byte[] bytes = (byte[]) mediaData.get("bytes");
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

        float conversionFactor = 0.25f;

        if (opts.outWidth > opts.outHeight)
            conversionFactor = 0.40f;

        byte[] finalBytes = ih.createThumbnail(bytes, String.valueOf((int) (width * conversionFactor)),
                (String) mediaData.get("orientation"), true);

        if (finalBytes == null) {
            Toast.makeText(EditPostActivity.this, getResources().getText(R.string.out_of_memory), Toast.LENGTH_SHORT).show();
            return;
        }

        resizedBitmap = BitmapFactory.decodeByteArray(finalBytes, 0, finalBytes.length);

        int selectionStart = mContentEditText.getSelectionStart();
        mStyleStart = selectionStart;
        int selectionEnd = mContentEditText.getSelectionEnd();

        if (selectionStart > selectionEnd) {
            int temp = selectionEnd;
            selectionEnd = selectionStart;
            selectionStart = temp;
        }

        Editable s = mContentEditText.getText();
        WPImageSpan is = new WPImageSpan(EditPostActivity.this, resizedBitmap, curStream);

        String imageWidth = WordPress.currentBlog.getMaxImageWidth();
        if (!imageWidth.equals("Original Size")) {
            try {
                is.setWidth(Integer.valueOf(imageWidth));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        is.setTitle((String) mediaData.get("title"));
        is.setImageSource(curStream);
        if (imgPath.contains("video")) {
            is.setVideo(true);
        }

        int line = 0, column = 0;
        try {
            line = mContentEditText.getLayout().getLineForOffset(selectionStart);
            column = mContentEditText.getSelectionStart() - mContentEditText.getLayout().getLineStart(line);
        } catch (Exception ex) {
        }

        WPImageSpan[] image_spans = s.getSpans(selectionStart, selectionEnd, WPImageSpan.class);
        if (image_spans.length != 0) {
            // insert a few line breaks if the cursor is already on an image
            s.insert(selectionEnd, "\n\n");
            selectionStart = selectionStart + 2;
            selectionEnd = selectionEnd + 2;
        } else if (column != 0) {
            // insert one line break if the cursor is not at the first column
            s.insert(selectionEnd, "\n");
            selectionStart = selectionStart + 1;
            selectionEnd = selectionEnd + 1;
        }

        s.insert(selectionStart, " ");
        s.setSpan(is, selectionStart, selectionEnd + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlignmentSpan.Standard as = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
        s.setSpan(as, selectionStart, selectionEnd + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.insert(selectionEnd + 1, "\n\n");
        try {
            mContentEditText.setSelection(s.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SpannableStringBuilder addMediaFromShareAction(String imgPath, Uri curStream, SpannableStringBuilder ssb) {
        initBlog();
        Bitmap resizedBitmap = null;
        String imageTitle = "";

        ImageHelper ih = new ImageHelper();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        Map<String, Object> mediaData = ih.getImageBytesForPath(imgPath, EditPostActivity.this);

        if (mediaData == null) {
            // data stream not returned
            return null;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        byte[] bytes = (byte[]) mediaData.get("bytes");
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

        float conversionFactor = 0.25f;

        if (opts.outWidth > opts.outHeight)
            conversionFactor = 0.40f;

        byte[] finalBytes = ih.createThumbnail((byte[]) mediaData.get("bytes"), String.valueOf((int) (width * conversionFactor)),
                (String) mediaData.get("orientation"), true);

        if (finalBytes == null) {
            Toast.makeText(EditPostActivity.this, getResources().getText(R.string.file_error_encountered), Toast.LENGTH_SHORT).show();
            return null;
        }

        resizedBitmap = BitmapFactory.decodeByteArray(finalBytes, 0, finalBytes.length);

        WPImageSpan is = new WPImageSpan(EditPostActivity.this, resizedBitmap, curStream);

        String imageWidth = WordPress.currentBlog.getMaxImageWidth();
        if (!imageWidth.equals("Original Size")) {
            try {
                is.setWidth(Integer.valueOf(imageWidth));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        is.setTitle(imageTitle);
        is.setImageSource(curStream);
        is.setVideo(imgPath.contains("video"));
        ssb.append(" ");
        ssb.setSpan(is, ssb.length() - 1, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlignmentSpan.Standard as = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
        ssb.setSpan(as, ssb.length() - 1, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append("\n");
        return ssb;
    }

    private String getCategoriesCSV() {
        String csv = "";
        if (mCategories.length() > 0) {
            for (int i = 0; i < mCategories.length(); i++) {
                try {
                    csv += EscapeUtils.unescapeHtml(mCategories.getString(i)) + ",";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            csv = csv.substring(0, csv.length() - 1);
        }
        return csv;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            showDialog(ID_DIALOG_TIME);
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hour, int minute) {
            mHour = hour;
            mMinute = minute;

            Date d = new Date(mYear - 1900, mMonth, mDay, mHour, mMinute);
            long timestamp = d.getTime();

            try {
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                String formattedDate = DateUtils.formatDateTime(EditPostActivity.this, timestamp, flags);
                mCustomPubDate = timestamp;
                mPubDateText.setText(formattedDate);
                mIsCustomPubDate = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable autoSaveRunnable = new Runnable() {
        @Override
        public void run() {
            savePost(true);
            mAutoSaveHandler.postDelayed(this, AUTOSAVE_DELAY_MILLIS);
        }
    };

    @Override
    public void afterTextChanged(Editable s) {

        try {
            int position = Selection.getSelectionStart(mContentEditText.getText());
            if ((mIsBackspace && position != 1) || mLastPosition == position || !mLocalDraft)
                return;

            if (position < 0) {
                position = 0;
            }
            mLastPosition = position;
            if (position > 0) {

                if (mStyleStart > position) {
                    mStyleStart = position - 1;
                }
                boolean exists = false;
                if (mBoldToggleButton.isChecked()) {
                    StyleSpan[] ss = s.getSpans(mStyleStart, position, StyleSpan.class);
                    exists = false;
                    for (int i = 0; i < ss.length; i++) {
                        if (ss[i].getStyle() == android.graphics.Typeface.BOLD) {
                            exists = true;
                        }
                    }
                    if (!exists)
                        s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), mStyleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                if (mEmToggleButton.isChecked()) {
                    StyleSpan[] ss = s.getSpans(mStyleStart, position, StyleSpan.class);
                    exists = false;
                    for (int i = 0; i < ss.length; i++) {
                        if (ss[i].getStyle() == android.graphics.Typeface.ITALIC) {
                            exists = true;
                        }
                    }
                    if (!exists)
                        s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), mStyleStart, position,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                if (mEmToggleButton.isChecked()) {
                    StyleSpan[] ss = s.getSpans(mStyleStart, position, StyleSpan.class);
                    exists = false;
                    for (int i = 0; i < ss.length; i++) {
                        if (ss[i].getStyle() == android.graphics.Typeface.ITALIC) {
                            exists = true;
                        }
                    }
                    if (!exists)
                        s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), mStyleStart, position,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                if (mUnderlineToggleButton.isChecked()) {
                    WPUnderlineSpan[] ss = s.getSpans(mStyleStart, position, WPUnderlineSpan.class);
                    exists = false;
                    for (int i = 0; i < ss.length; i++) {
                        exists = true;
                    }
                    if (!exists)
                        s.setSpan(new WPUnderlineSpan(), mStyleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                if (mStrikeToggleButton.isChecked()) {
                    StrikethroughSpan[] ss = s.getSpans(mStyleStart, position, StrikethroughSpan.class);
                    exists = false;
                    for (int i = 0; i < ss.length; i++) {
                        exists = true;
                    }
                    if (!exists)
                        s.setSpan(new StrikethroughSpan(), mStyleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                if (mBquoteToggleButton.isChecked()) {

                    QuoteSpan[] ss = s.getSpans(mStyleStart, position, QuoteSpan.class);
                    exists = false;
                    for (int i = 0; i < ss.length; i++) {
                        exists = true;
                    }
                    if (!exists)
                        s.setSpan(new QuoteSpan(), mStyleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        if ((count - after == 1) || (s.length() == 0))
            mIsBackspace = true;
        else
            mIsBackspace = false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void onSelectionChanged() {
        if (!mLocalDraft)
            return;

        final Spannable s = mContentEditText.getText();
        // set toggle buttons if cursor is inside of a matching span
        mStyleStart = mContentEditText.getSelectionStart();
        Object[] spans = s.getSpans(mContentEditText.getSelectionStart(), mContentEditText.getSelectionStart(), Object.class);

        mBoldToggleButton.setChecked(false);
        mEmToggleButton.setChecked(false);
        mBquoteToggleButton.setChecked(false);
        mUnderlineToggleButton.setChecked(false);
        mStrikeToggleButton.setChecked(false);
        for (Object span : spans) {
            if (span instanceof StyleSpan) {
                StyleSpan ss = (StyleSpan) span;
                if (ss.getStyle() == android.graphics.Typeface.BOLD) {
                    mBoldToggleButton.setChecked(true);
                }
                if (ss.getStyle() == android.graphics.Typeface.ITALIC) {
                    mEmToggleButton.setChecked(true);
                }
            }
            if (span instanceof QuoteSpan) {
                mBquoteToggleButton.setChecked(true);
            }
            if (span instanceof WPUnderlineSpan) {
                mUnderlineToggleButton.setChecked(true);
            }
            if (span instanceof StrikethroughSpan) {
                mStrikeToggleButton.setChecked(true);
            }
        }
    }
    
    
    
    
    
    ///added this for multichoice dialog   MOCA for android
   
    
    protected void showSelectionDialog(String postChoices, CharSequence[] choices,Button buttonSelector ) {
        Choices = choices;
        strdButton = buttonSelector;
        boolean[] checkedSelection = new boolean[Choices.length];
        int count = Choices.length;
        selectedChoices.clear();
        
        
        //loads array if post is not new
        if (!mIsNew ){
            String[] options = postChoices.split(":");
            for (int k=0; k < options.length;k++){
                selectedChoices.add(options[k].trim());
            }
        }

        for(int i = 0; i < count; i++)
            checkedSelection[i] = selectedChoices.contains(Choices[i]);


        DialogInterface.OnMultiChoiceClickListener selectionDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked) {selectedChoices.add(Choices[which]);}
                else {selectedChoices.remove(Choices[which]);}
                onChangeSelection();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select all that apply");
        builder.setMultiChoiceItems(Choices, checkedSelection, selectionDialogListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    protected void onChangeSelection() {
        StringBuilder stringBuilder = new StringBuilder();

        for(CharSequence area_assessed : selectedChoices){
            if (stringBuilder.toString().equals(""))
                stringBuilder.append(area_assessed);
            else 
                stringBuilder.append( ":"+ area_assessed );
        }
        strdButton.setText("");
        strdButton.setText(stringBuilder.toString());
    }
    
    protected void onChangeToggleButton(ToggleButton toggleButton){
        if (!toggleButton.isChecked()){
            toggleButton.setChecked(false);
        } else {
            toggleButton.setChecked(true);
        }
    }
    
    
    public void chooseDate(){
        new DatePickerDialog(EditPostActivity.this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
            dateTime.set(Calendar.YEAR,year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    
    private void updateLabel() {
        //timeLabel.setText(formatDateTime.format(dateTime.getTime()));
    }
    
//    private DatePickerDialog customDatePicker() {
//        DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListner,
//                mYear, mMonth, mDay);
//        try {
//            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
//            for (Field datePickerDialogField : datePickerDialogFields) {
//                if (datePickerDialogField.getName().equals("mDatePicker")) {
//                    datePickerDialogField.setAccessible(true);
//                    DatePicker datePicker = (DatePicker) datePickerDialogField
//                            .get(dpd);
//                    Field datePickerFields[] = datePickerDialogField.getType()
//                            .getDeclaredFields();
//                    for (Field datePickerField : datePickerFields) {
//                        if ("mDayPicker".equals(datePickerField.getName())
//                                || "mDaySpinner".equals(datePickerField
//                                        .getName())) {
//                            datePickerField.setAccessible(true);
//                            Object dayPicker = new Object();
//                            dayPicker = datePickerField.get(datePicker);
//                            ((View) dayPicker).setVisibility(View.GONE);
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//        }
//        return dpd;
  //  }

    
    
    
    ///end added
    
    
}