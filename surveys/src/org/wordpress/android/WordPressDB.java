package org.wordpress.android;

import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;

import org.wordpress.android.models.MediaFile;
import org.wordpress.android.models.Post;
import org.wordpress.android.ui.posts.EditPostActivity;

public class WordPressDB {

    private static final int DATABASE_VERSION = 16;

    private static final String CREATE_TABLE_SETTINGS = "create table if not exists accounts (id integer primary key autoincrement, "
            + "url text, blogName text, username text, password text, imagePlacement text, centerThumbnail boolean, fullSizeImage boolean, maxImageWidth text, maxImageWidthId integer, lastCommentId integer, runService boolean);";
    private static final String CREATE_TABLE_EULA = "create table if not exists eula (id integer primary key autoincrement, "
            + "read integer not null, interval text, statsdate integer);";
    private static final String CREATE_TABLE_MEDIA = "create table if not exists media (id integer primary key autoincrement, "
            + "postID integer not null, filePath text default '', fileName text default '', title text default '', description text default '', caption text default '', horizontalAlignment integer default 0, width integer default 0, height integer default 0, mimeType text default '', featured boolean default false, isVideo boolean default false);";
    private static final String SETTINGS_TABLE = "accounts";
    private static final String DATABASE_NAME = "wordpress";
    private static final String MEDIA_TABLE = "media";

    private static final String CREATE_TABLE_POSTS = "create table if not exists posts (id integer primary key autoincrement, blogID text, "
            + "postid text, title text default '', dateCreated date, date_created_gmt date, categories text default '', custom_fields text default '', "
            + "description text default '', link text default '', mt_allow_comments boolean, mt_allow_pings boolean, "
            + "mt_excerpt text default '', mt_keywords text default '', mt_text_more text default '', permaLink text default '',"
            + "post_status text default '', userid integer default 0, wp_author_display_name text default '', wp_author_id text default '',"
            + "wp_password text default '', wp_post_format text default '', wp_slug text default '', mediaPaths text default '', "
            + "latitude real, longitude real, localDraft boolean default 0, uploaded boolean default 0, isPage boolean default 0,"
            + "wp_page_parent_id text, wp_page_parent_title text, " 
            + "rbca_asser_name text default '', rbca_asser_org text default '', rbca_asser_email text default '', rbca_asser_phone text default '', " 
            + "rbca_coord_latitude real, rbca_coord_longitude real, rbca_coord_altitude real, rbca_coord_accuracy real,"
            + "rbca_coord_loc text default '', rbca_coord_loc_oth text default '', rbca_coord_corner text default '', rbca_coord_notes text default '', "
            + "rbca_addr_no text default '', rbca_addr_street text default '',rbca_addr_notes text default '',rbca_img_right text default '', "
            + "rbca_img_front text default '', rbca_img_right text default '', rbca_bldg_name text default '', rbca_is_extant text default '', "
            + "rbca_bldg_area text default '',rbca_bldg_posting text default '', rbca_bldg_posting_oth text default '',  "
            + "rbca_bldg_posting_img text default '', rbca_bldg_occucy text default '',"
            + "rbca_bldg_occucy_avail integer default 0, rbca_bldg_stories real, rbca_bldg_width real,"
            + "rbca_bldg_length real, rbca_bldg_use text default '', rbca_bldg_use_oth text default '',"
            + "rbca_bldg_outbldg integer default 0, rbca_bldg_outbldg_notes text default '',"
            + "rbca_bldg_units_res integer default 0, rbca_bldg_units_comm integer default 0,"
            + "rbca_bldg_occu_name text default '', rbca_bldg_occu_phone integer default 0,"
            + "rbca_bldg_notes text default '', rbca_hist_desig text default '', rbca_hist_desig_oth text default '' ," 
            + "rbca_hist_dist text default '', rbca_hist_dist_name text default '', "
            + "rbca_hist_appear integer default 0, rbca_hist_age integer default 0, rbca_hist_age_meta text default '', "
            + "rbca_hist_yr_built integer default 0, rbca_hist_age_src integer default 0, rbca_hist_age_src_oth text default '' ,"
            + "rbca_hist_notes text default '', rbca_dmg_source text default '', rbca_dmg_source_oth text default '', " 
            + "rbca_dmg_total integer default 0, rbca_dmg_desc text default '', rbca_struct_type text default '', "
            + "rbca_struct_type_oth text default '', rbca_struct_defects text default '', rbca_struct integer default 0, "
            + "rbca_struct_notes text default '', rbca_found_type text default '', rbca_found_type_oth text default '', "
            + "rbca_found integer default 0, rbca_found_notes text default '', rbca_extwall_mat text default '', "
            + "rbca_extwall_mat_oth text default '', rbca_extwall integer default 0, rbca_extwall_notes text default '', "
            + "rbca_extfeat_type text default '', rbca_extfeat_type_oth text default '', rbca_extfeat integer default 0, "
            + "rbca_extfeat_notes text default '', rbca_win_type text default '', rbca_win_type_oth text default '', "
            + "rbca_win_mat text default '', rbca_win_mat_oth text default '', rbca_win integer default 0, rbca_win_notes text default '', "
            + "rbca_roof_type text default '', rbca_roof_type_oth text default '', rbca_roof_mat text default '', "
            + "rbca_roof_mat_oth text default '', rbca_roof integer default 0, rbca_roof_notes text default '', "
            + "rbca_int_cond text default '', rbca_int_collect_extant integer default 0, rbca_int_collect_type text default '', "
            + "rbca_int_collect_type_oth text default '',rbca_int_img1 text default '', rbca_int_desc1 text default '', "
            + "rbca_int_img2 text default '', rbca_int_desc2 text default '',rbca_int_img3 text default '',rbca_int_desc3 text default '', "
            + "rbca_int_notes text default '', rbca_landveg_feat text default '', rbca_landveg_feat_oth text default '', "
            + "rbca_landveg integer default 0, rbca_landveg_notes text default '', rbca_landblt_feat text default '', "
            + "rbca_landblt_feat_oth text default '', rbca_landblt integer default 0, rbca_landblt_notes text default '', "
            + "rbca_media_img1 text default '', rbca_media_desc1 text default '', rbca_media_img2 text default '', "
            + "rbca_media_desc2 text default '', rbca_media_img3 text default '', rbca_media_desc3 text default '', "
            + "rbca_media_img4 text default '', rbca_media_desc4 text default '', rbca_media_img5 text default '', "
            + "rbca_media_desc5 text default '', rbca_media_img6 text default '', rbca_media_desc6 text default '', "
            + "rbca_hzrd integer default 0, rbca_hzrd_type text default '', "
            + "rbca_hzrd_type_oth text default '', rbca_hzrd_notes text default '', rbca_hzrd_hazmat text default '', "
            + "rbca_hzrd_hazmat_oth text default '', rbca_actn text default '', rbca_actn_oth text default '', "
            + "rbca_eval text default '', rbca_eval_oth text default '' );"; 
            		

    private static final String CREATE_TABLE_COMMENTS = "create table if not exists comments (blogID text, postID text, iCommentID integer, author text, comment text, commentDate text, commentDateFormatted text, status text, url text, email text, postTitle text);";
    private static final String POSTS_TABLE = "posts";
    private static final String COMMENTS_TABLE = "comments";

    // eula
    private static final String EULA_TABLE = "eula";

    // categories
    private static final String CREATE_TABLE_CATEGORIES = "create table if not exists cats (id integer primary key autoincrement, "
            + "blog_id text, wp_id integer, category_name text not null);";
    private static final String CATEGORIES_TABLE = "cats";

    // for capturing blogID, trac ticket #
    private static final String ADD_BLOGID = "alter table accounts add blogId integer;";
    private static final String UPDATE_BLOGID = "update accounts set blogId = 1;";

    // add notification options
    private static final String ADD_SOUND_OPTION = "alter table eula add sound boolean default false;";
    private static final String ADD_VIBRATE_OPTION = "alter table eula add vibrate boolean default false;";
    private static final String ADD_LIGHT_OPTION = "alter table eula add light boolean default false;";
    private static final String ADD_TAGLINE = "alter table eula add tagline text;";
    private static final String ADD_TAGLINE_FLAG = "alter table eula add tagline_flag boolean default false;";

    // for capturing blogID, trac ticket #
    private static final String ADD_LOCATION_FLAG = "alter table accounts add location boolean default false;";

    // fix commentID data type
    private static final String ADD_NEW_COMMENT_ID = "ALTER TABLE comments ADD iCommentID INTEGER;";
    private static final String COPY_COMMENT_IDS = "UPDATE comments SET iCommentID = commentID;";

    // add wordpress.com stats login info
    private static final String ADD_DOTCOM_USERNAME = "alter table accounts add dotcom_username text;";
    private static final String ADD_DOTCOM_PASSWORD = "alter table accounts add dotcom_password text;";
    private static final String ADD_API_KEY = "alter table accounts add api_key text;";
    private static final String ADD_API_BLOGID = "alter table accounts add api_blogid text;";

    // add wordpress.com flag and version column
    private static final String ADD_DOTCOM_FLAG = "alter table accounts add dotcomFlag boolean default false;";
    private static final String ADD_WP_VERSION = "alter table accounts add wpVersion text;";

    // add httpuser and httppassword
    private static final String ADD_HTTPUSER = "alter table accounts add httpuser text;";
    private static final String ADD_HTTPPASSWORD = "alter table accounts add httppassword text;";

    // add new unique identifier to no longer use device imei
    private static final String ADD_UNIQUE_ID = "alter table eula add uuid text;";

    // add new table for QuickPress homescreen shortcuts
    private static final String CREATE_TABLE_QUICKPRESS_SHORTCUTS = "create table if not exists quickpress_shortcuts (id integer primary key autoincrement, accountId text, name text);";
    private static final String QUICKPRESS_SHORTCUTS_TABLE = "quickpress_shortcuts";

    // add field to store last used blog
    private static final String ADD_LAST_BLOG_ID = "alter table eula add last_blog_id text;";

    // add field to store last used blog
    private static final String ADD_POST_FORMATS = "alter table accounts add postFormats text default '';";

    //add scaled image settings
    private static final String ADD_SCALED_IMAGE = "alter table accounts add isScaledImage boolean default false;";
    private static final String ADD_SCALED_IMAGE_IMG_WIDTH = "alter table accounts add scaledImgWidth integer default 1024;";

    //add boolean to posts to check uploaded posts that have local changes
    private static final String ADD_LOCAL_POST_CHANGES = "alter table posts add isLocalChange boolean default 0";

    //add boolean to track if featured image should be included in the post content
    private static final String ADD_FEATURED_IN_POST = "alter table media add isFeaturedInPost boolean default false;";

    // add home url to blog settings
    private static final String ADD_HOME_URL = "alter table accounts add homeURL text default '';";

    private static final String ADD_BLOG_OPTIONS = "alter table accounts add blog_options text default '';";

    private SQLiteDatabase db;

    protected static final String PASSWORD_SECRET = "nottherealpasscode";

    public String defaultBlog = "";

    private Context context;

    @SuppressLint("NewApi")
	public WordPressDB(Context ctx) {
        this.context = ctx;

        try {
            db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        } catch (SQLiteException e) {
            db = null;
            return;
        }

        // db.execSQL("DROP TABLE IF EXISTS "+ SETTINGS_TABLE);
        db.execSQL(CREATE_TABLE_SETTINGS);
        // added eula to this class to fix trac #49
        db.execSQL(CREATE_TABLE_EULA);

        db.execSQL(CREATE_TABLE_POSTS);
        db.execSQL(CREATE_TABLE_COMMENTS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_QUICKPRESS_SHORTCUTS);
        db.execSQL(CREATE_TABLE_MEDIA);

        try {
            if (db.getVersion() < 1) { // user is new install
                db.execSQL(ADD_BLOGID);
                db.execSQL(UPDATE_BLOGID);
                db.execSQL(ADD_SOUND_OPTION);
                db.execSQL(ADD_VIBRATE_OPTION);
                db.execSQL(ADD_LIGHT_OPTION);
                db.execSQL(ADD_LOCATION_FLAG);
                db.execSQL(ADD_TAGLINE);
                db.execSQL(ADD_TAGLINE_FLAG);
                db.execSQL(ADD_DOTCOM_USERNAME);
                db.execSQL(ADD_DOTCOM_PASSWORD);
                db.execSQL(ADD_API_KEY);
                db.execSQL(ADD_API_BLOGID);
                db.execSQL(ADD_DOTCOM_FLAG);
                db.execSQL(ADD_WP_VERSION);
                db.execSQL(ADD_UNIQUE_ID);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                db.setVersion(DATABASE_VERSION); // set to latest revision
            } else if (db.getVersion() == 1) { // v1.0 or v1.0.1
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_BLOGID);
                db.execSQL(UPDATE_BLOGID);
                db.execSQL(ADD_SOUND_OPTION);
                db.execSQL(ADD_VIBRATE_OPTION);
                db.execSQL(ADD_LIGHT_OPTION);
                db.execSQL(ADD_LOCATION_FLAG);
                db.execSQL(ADD_TAGLINE);
                db.execSQL(ADD_TAGLINE_FLAG);
                db.execSQL(ADD_NEW_COMMENT_ID);
                db.execSQL(COPY_COMMENT_IDS);
                db.execSQL(ADD_DOTCOM_USERNAME);
                db.execSQL(ADD_DOTCOM_PASSWORD);
                db.execSQL(ADD_API_KEY);
                db.execSQL(ADD_API_BLOGID);
                db.execSQL(ADD_DOTCOM_FLAG);
                db.execSQL(ADD_WP_VERSION);
                db.execSQL(ADD_UNIQUE_ID);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 2) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_SOUND_OPTION);
                db.execSQL(ADD_VIBRATE_OPTION);
                db.execSQL(ADD_LIGHT_OPTION);
                db.execSQL(ADD_LOCATION_FLAG);
                db.execSQL(ADD_TAGLINE);
                db.execSQL(ADD_TAGLINE_FLAG);
                db.execSQL(ADD_NEW_COMMENT_ID);
                db.execSQL(COPY_COMMENT_IDS);
                db.execSQL(ADD_DOTCOM_USERNAME);
                db.execSQL(ADD_DOTCOM_PASSWORD);
                db.execSQL(ADD_API_KEY);
                db.execSQL(ADD_API_BLOGID);
                db.execSQL(ADD_DOTCOM_FLAG);
                db.execSQL(ADD_WP_VERSION);
                db.execSQL(ADD_UNIQUE_ID);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 3) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_LOCATION_FLAG);
                db.execSQL(ADD_TAGLINE);
                db.execSQL(ADD_TAGLINE_FLAG);
                db.execSQL(ADD_NEW_COMMENT_ID);
                db.execSQL(COPY_COMMENT_IDS);
                db.execSQL(ADD_DOTCOM_USERNAME);
                db.execSQL(ADD_DOTCOM_PASSWORD);
                db.execSQL(ADD_API_KEY);
                db.execSQL(ADD_API_BLOGID);
                db.execSQL(ADD_DOTCOM_FLAG);
                db.execSQL(ADD_WP_VERSION);
                db.execSQL(ADD_UNIQUE_ID);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 4) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_LOCATION_FLAG);
                db.execSQL(ADD_TAGLINE);
                db.execSQL(ADD_TAGLINE_FLAG);
                db.execSQL(ADD_NEW_COMMENT_ID);
                db.execSQL(COPY_COMMENT_IDS);
                db.execSQL(ADD_DOTCOM_USERNAME);
                db.execSQL(ADD_DOTCOM_PASSWORD);
                db.execSQL(ADD_API_KEY);
                db.execSQL(ADD_API_BLOGID);
                db.execSQL(ADD_DOTCOM_FLAG);
                db.execSQL(ADD_WP_VERSION);
                db.execSQL(ADD_UNIQUE_ID);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 5) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_TAGLINE);
                db.execSQL(ADD_TAGLINE_FLAG);
                db.execSQL(ADD_NEW_COMMENT_ID);
                db.execSQL(COPY_COMMENT_IDS);
                db.execSQL(ADD_DOTCOM_USERNAME);
                db.execSQL(ADD_DOTCOM_PASSWORD);
                db.execSQL(ADD_API_KEY);
                db.execSQL(ADD_API_BLOGID);
                db.execSQL(ADD_DOTCOM_FLAG);
                db.execSQL(ADD_WP_VERSION);
                db.execSQL(ADD_UNIQUE_ID);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 6) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_NEW_COMMENT_ID);
                db.execSQL(COPY_COMMENT_IDS);
                db.execSQL(ADD_DOTCOM_USERNAME);
                db.execSQL(ADD_DOTCOM_PASSWORD);
                db.execSQL(ADD_API_KEY);
                db.execSQL(ADD_API_BLOGID);
                db.execSQL(ADD_DOTCOM_FLAG);
                db.execSQL(ADD_WP_VERSION);
                db.execSQL(ADD_UNIQUE_ID);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 7) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_UNIQUE_ID);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 8) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 9) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);
                db.execSQL(ADD_HTTPUSER);
                db.execSQL(ADD_HTTPPASSWORD);
                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePasswords();
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 10) {
                db.delete(POSTS_TABLE, null, null);
                db.execSQL(CREATE_TABLE_POSTS);

//                try {
//                    // migrate drafts
//
//                    Cursor c = db.query("localdrafts", new String[] { "blogID",
//                            "title", "content", "picturePaths", "date",
//                            "categories", "tags", "status", "password",
//                            "latitude", "longitude" ,"rbca_coord_loc","rbca_coord_loc_oth", 
//                            "rbca_coord_corner", "rbca_coord_notes", "rbca_addr_no", "rbca_addr_street",
//                            "rbca_bldg_area","rbca_bldg_posting","rbca_bldg_posting_oth","rbca_bldg_occucy","rbca_bldg_occucy_avail", "rbca_bldg_stories", 
//                            "rbca_bldg_width","rbca_bldg_length","rbca_bldg_use","rbca_bldg_use_oth","rbca_bldg_outbldg", "rbca_bldg_outbldg_notes", "rbca_bldg_units_res", 
//                            "rbca_bldg_units_comm", "rbca_bldg_occu_name", "rbca_bldg_occu_phone", "rbca_bldg_notes", "rbca_hist_desig","rbca_hist_desig_oth",
//                            "rbca_hist_dist", "rbca_hist_dist_name", "rbca_hist_appear","rbca_hist_age","rbca_hist_age_meta", "rbca_hist_yr_built", "rbca_hist_age_src",
//                            "rbca_hist_age_src_oth","rbca_hist_notes","rbca_dmg_source", "rbca_dmg_source_oth","rbca_dmg_total", "rbca_dmg_desc",
//                            "rbca_struct_type","rbca_struct_type_oth","rbca_struct_defects",
//                            "rbca_struct","rbca_struct_notes","rbca_found_type","rbca_found_type_oth","rbca_found","rbca_found_notes","rbca_extwall_mat","rbca_extwall_mat_oth",
//                            "rbca_extwall","rbca_extwall_notes","rbca_extfeat_type","rbca_extfeat_type_oth","rbca_extfeat","rbca_extfeat_notes","rbca_win_type",
//                            "rbca_win_type_oth","rbca_win_mat","rbca_win_mat_oth","rbca_win","rbca_win_notes","rbca_roof_type","rbca_roof_type_oth","rbca_roof_mat",
//                            "rbca_roof_mat","rbca_roof_mat_oth","rbca_roof","rbca_roof_notes","rbca_int_cond","rbca_int_collect_extant","rbca_int_collect_type","rbca_int_collect_type_oth",
//                            "rbca_int_notes","rbca_landveg_feat","rbca_landveg_feat_oth","rbca_landveg","rbca_landveg_notes","rbca_landblt_feat","rbca_landblt_feat_oth",
//                            "rbca_landblt","rbca_landblt_notes","rbca_hzrd","rbca_hzrd_type","rbca_hzrd_type_oth","rbca_hzrd_notes","rbca_hzrd_hazmat",
//                            "rbca_hzrd_hazmat_oth","rbca_actn","rbca_actn_oth","rbca_eval","rbca_eval_oth"}, null, null, null, null,
//                           
//                            "id desc");
//                    int numRows = c.getCount();
//                    c.moveToFirst();
//                    
//                    for (int k=0;k<c.getCount();k++){
//                        System.out.println("MigrateDrafts_Linea :"+k+" ColumnName :"+c.getColumnName(k)+" DataType :"+c.getType(k));
//                    }
//
//                    for (int i = 0; i < numRows; ++i) {
//                        if (c.getString(0) != null) {
//                            Post post = new Post(c.getInt(0), c.getString(1),
//                                    c.getString(2), c.getString(3),c.getLong(4), c.getString(5),
//                                    c.getString(6), c.getString(7),c.getString(8), c.getDouble(9),
//                                    c.getDouble(10), false,"", false, false,
//                                    c.getString(12), c.getString(13),c.getString(14),
//                                    c.getString(15),c.getString(16),c.getString(17), c.getString(18),
//                                    c.getString(19),c.getString(20),c.getString(21),c.getInt(22),c.getDouble(23),
//                                    c.getDouble(24),c.getDouble(25),c.getString(26),c.getString(27),c.getInt(28),
//                                    c.getString(29),c.getInt(30),c.getInt(31),c.getString(32),c.getInt(33),
//                                    c.getString(34),c.getString(35),c.getString(36), c.getString(37), c.getString(38),
//                                    c.getInt(39),c.getInt(40),c.getString(41),c.getInt(42),c.getInt(43),c.getString(44),c.getString(45),c.getInt(46),
//                                    c.getString(47),c.getString(48),c.getString(49),c.getString(50),c.getString(51),c.getDouble(52),c.getString(53),
//                                    c.getString(54),c.getString(55),c.getString(56),c.getString(57),c.getInt(58),c.getString(59),c.getString(60));
//                            post.setLocalDraft(true);
//                            post.setPost_status("localdraft");
//                            savePost(post, c.getInt(0));
//                        }
//                        c.moveToNext();
//                    }
//                    c.close();

                    db.delete("localdrafts", null, null);

                    // pages
//                    c = db.query("localpagedrafts", new String[] { "blogID",
//                            "title", "content", "picturePaths", "date",
//                            "status", "password" ,"rbca_coord_loc","rbca_coord_loc_oth", 
//                            "rbca_coord_corner", "rbca_coord_notes", "rbca_addr_no", 
//                            "rbca_addr_street","rbca_bldg_area", "rbca_bldg_posting","rbca_bldg_posting_oth", "rbca_bldg_occucy",
//                            "rbca_bldg_occucy_avail","rbca_bldg_stories", "rbca_bldg_width","rbca_bldg_length","rbca_bldg_use",
//                            "rbca_bldg_use_oth","rbca_bldg_outbldg", "rbca_bldg_outbldg_notes", "rbca_bldg_units_res", 
//                            "rbca_bldg_units_comm", "rbca_bldg_occu_name", "rbca_bldg_occu_phone", "rbca_bldg_notes", "rbca_hist_desig", "rbca_hist_desig_oth", 
//                            "rbca_hist_dist", "rbca_hist_dist_name","rbca_hist_appear","rbca_hist_age","rbca_hist_age_meta", "rbca_hist_yr_built", "rbca_dmg_date",
//                            "rbca_dmg_source","rbca_dmg_source_oth","rbca_dmg_total", "rbca_dmg_desc","rbca_flood_water","rbca_flood_water_oth","rbca_flood_entry",
//                            "rbca_flood_entry_oth","rbca_flood_depth","rbca_flood_sed","rbca_flood_sed_oth","rbca_flood_notes","rbca_struct_type","rbca_struct_type_oth",
//                            "rbca_struct","rbca_struct_notes","rbca_found_type","rbca_found_type_oth","rbca_found","rbca_found_notes","rbca_extwall_mat","rbca_extwall_mat_oth",
//                            "rbca_extwall","rbca_extwall_notes","rbca_extfeat_type","rbca_extfeat_type_oth","rbca_extfeat","rbca_extfeat_notes","rbca_win_type",
//                            "rbca_win_type_oth","rbca_win_mat","rbca_win_mat_oth","rbca_win","rbca_win_notes","rbca_roof_type","rbca_roof_type_oth","rbca_roof_mat",
//                            "rbca_roof_mat","rbca_roof_mat_oth","rbca_roof","rbca_roof_notes","rbca_int_cond","rbca_int_collect_extant","rbca_int_collect_type","rbca_int_collect_type_oth",
//                            "rbca_int_notes","rbca_landveg_feat","rbca_landveg_feat_oth","rbca_landveg","rbca_landveg_notes","rbca_landblt_feat","rbca_landblt_feat_oth",
//                            "rbca_landblt","rbca_landblt_notes"}, null, null, null, null,
//                            "id desc");
//                    numRows = c.getCount();
//                    c.moveToFirst();
//
//                    for (int i = 0; i < numRows; ++i) {
//                        if (c.getString(0) != null) {
//                            Post post = new Post(c.getInt(0), c.getString(1),
//                                    c.getString(2), c.getString(3),
//                                    c.getLong(4), c.getString(5), "", "",
//                                    c.getString(6), 0, 0, true, "", false, false,
//                                    c.getString(8),c.getString(9),c.getString(10),
//                                    c.getString(11),c.getString(12),c.getString(13),c.getString(14),
//                                    c.getString(15),c.getString(16),c.getString(17),c.getInt(18),
//                                    c.getDouble(19),c.getDouble(20),c.getDouble(21),c.getString(22), 
//                                    c.getString(23),c.getInt(24),c.getString(25),c.getInt(36),c.getInt(37),
//                                    c.getString(38),c.getInt(39),c.getString(40),c.getString(41), c.getString(42),
//                                    c.getString(43), c.getString(44),c.getInt(45),c.getInt(46),c.getString(47),c.getInt(48),
//                                    c.getInt(49),c.getString(50),c.getString(51),c.getInt(52),c.getString(53),c.getString(54),c.getString(55),
//                                    c.getString(56),c.getString(57),c.getDouble(58),c.getString(59),c.getString(60),c.getString(61),c.getString(62),
//                                    c.getString(63),c.getInt(64),c.getString(65),c.getString(66));
//                            post.setLocalDraft(true);
//                            post.setPost_status("localdraft");
//                            post.setPage(true);
//                            savePost(post, c.getInt(0));
//                        }
//                        c.moveToNext();
//                    }
//                    c.close();
//                    db.delete("localpagedrafts", null, null);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                db.execSQL(ADD_LAST_BLOG_ID);
                db.execSQL(ADD_POST_FORMATS);
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 11) {
                db.execSQL(ADD_SCALED_IMAGE);
                db.execSQL(ADD_SCALED_IMAGE_IMG_WIDTH);
                db.execSQL(ADD_LOCAL_POST_CHANGES);
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 12) {
                db.execSQL(ADD_FEATURED_IN_POST);
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 13) {
                db.execSQL(ADD_HOME_URL);
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 14) {
                db.execSQL(ADD_BLOG_OPTIONS);
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 15) {
                migratePreferences(ctx);
                db.setVersion(DATABASE_VERSION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    
    private void migratePreferences(Context ctx) {
        // Migrate preferences out of the db
        Map<?, ?> notificationOptions = getNotificationOptions();
        if (notificationOptions != null) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = settings.edit();
            String interval = getInterval();
            if (interval != "") {
                editor.putString("wp_pref_notifications_interval", interval);
            }
            editor.putBoolean("wp_pref_notification_sound", (notificationOptions.get("sound").toString().equals("1")) ? true : false);
            editor.putBoolean("wp_pref_notification_vibrate", (notificationOptions.get("vibrate").toString().equals("1")) ? true : false);
            editor.putBoolean("wp_pref_notification_light", (notificationOptions.get("light").toString().equals("1")) ? true : false);
            editor.putBoolean("wp_pref_signature_enabled", (notificationOptions.get("tagline_flag").toString().equals("1")) ? true : false);

            String tagline = notificationOptions.get("tagline").toString();
            if (tagline != "") {
                editor.putString("wp_pref_post_signature", tagline);
            }
            editor.commit();
        }
    }

    public long addAccount(String url, String homeURL, String blogName, String username,
            String password, String httpuser, String httppassword,
            String imagePlacement, boolean centerThumbnail,
            boolean fullSizeImage, String maxImageWidth, int maxImageWidthId,
            boolean runService, int blogId, boolean wpcom, String wpVersion) {

        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("homeURL", homeURL);
        values.put("blogName", blogName);
        values.put("username", username);
        values.put("password", encryptPassword(password));
        values.put("httpuser", httpuser);
        values.put("httppassword", encryptPassword(httppassword));
        values.put("imagePlacement", imagePlacement);
        values.put("centerThumbnail", centerThumbnail);
        values.put("fullSizeImage", fullSizeImage);
        values.put("maxImageWidth", maxImageWidth);
        values.put("maxImageWidthId", maxImageWidthId);
        values.put("runService", runService);
        values.put("blogId", blogId);
        values.put("dotcomFlag", wpcom);
        values.put("wpVersion", wpVersion);
        return db.insert(SETTINGS_TABLE, null, values);
    }

    public List<Map<String, Object>> getAccounts() {

        Cursor c = db.query(SETTINGS_TABLE, new String[] { "id", "blogName",
                "username", "runService", "blogId", "url" }, null, null, null,
                null, null);
        int id;
        String blogName, username, url;
        int blogId;
        int runService;
        int numRows = c.getCount();
        c.moveToFirst();
        List<Map<String, Object>> accounts = new Vector<Map<String, Object>>();
        for (int i = 0; i < numRows; i++) {

            id = c.getInt(0);
            blogName = c.getString(1);
            username = c.getString(2);
            runService = c.getInt(3);
            blogId = c.getInt(4);
            url = c.getString(5);
            if (id > 0) {
                Map<String, Object> thisHash = new HashMap<String, Object>();
                thisHash.put("id", id);
                thisHash.put("blogName", blogName);
                thisHash.put("username", username);
                thisHash.put("runService", runService);
                thisHash.put("blogId", blogId);
                thisHash.put("url", url);
                accounts.add(thisHash);
            }
            c.moveToNext();
        }
        c.close();

        return accounts;
    }

    public boolean checkMatch(String blogName, String blogURL, String username) {

        Cursor c = db.query(SETTINGS_TABLE, new String[] { "blogName", "url" },
                "blogName='" + addSlashes(blogName) + "' AND url='"
                        + addSlashes(blogURL) + "'" + " AND username='"
                        + username + "'", null, null, null, null);
        int numRows = c.getCount();
        boolean result = false;

        if (numRows > 0) {
            // this account is already saved, yo!
            result = true;
        }

        c.close();

        return result;
    }

    public static String addSlashes(String text) {
        final StringBuffer sb = new StringBuffer(text.length() * 2);
        final StringCharacterIterator iterator = new StringCharacterIterator(
                text);

        char character = iterator.current();

        while (character != StringCharacterIterator.DONE) {
            if (character == '"')
                sb.append("\\\"");
            else if (character == '\'')
                sb.append("\'\'");
            else if (character == '\\')
                sb.append("\\\\");
            else if (character == '\n')
                sb.append("\\n");
            else if (character == '{')
                sb.append("\\{");
            else if (character == '}')
                sb.append("\\}");
            else
                sb.append(character);

            character = iterator.next();
        }

        return sb.toString();
    }

    public boolean saveSettings(String id, String url, String homeURL, String username,
            String password, String httpuser, String httppassword,
            String imagePlacement, boolean isFeaturedImageCapable,
            boolean fullSizeImage, String maxImageWidth, int maxImageWidthId,
            boolean location, boolean isWPCom, String originalUsername,
            String postFormats, String dotcomUsername, String dotcomPassword,
            String apiBlogID, String apiKey, boolean isScaledImage, int scaledImgWidth, String blogOptions) {

        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("homeURL", homeURL);
        values.put("username", username);
        values.put("password", encryptPassword(password));
        values.put("httpuser", httpuser);
        values.put("httppassword", encryptPassword(httppassword));
        values.put("imagePlacement", imagePlacement);
        values.put("centerThumbnail", isFeaturedImageCapable);
        values.put("fullSizeImage", fullSizeImage);
        values.put("maxImageWidth", maxImageWidth);
        values.put("maxImageWidthId", maxImageWidthId);
        values.put("location", location);
        values.put("postFormats", postFormats);
        values.put("dotcom_username", dotcomUsername);
        values.put("dotcom_password", encryptPassword(dotcomPassword));
        values.put("api_blogid", apiBlogID);
        values.put("api_key", apiKey);
        values.put("isScaledImage", isScaledImage);
        values.put("scaledImgWidth", scaledImgWidth);
        values.put("blog_options", blogOptions);

        boolean returnValue = db.update(SETTINGS_TABLE, values, "id=" + id,
                null) > 0;
        if (isWPCom) {
            // update the login for other wordpress.com accounts
            ContentValues userPass = new ContentValues();
            userPass.put("username", username);
            userPass.put("password", encryptPassword(password));
            returnValue = db.update(SETTINGS_TABLE, userPass, "username=\""
                    + originalUsername + "\" AND dotcomFlag=1", null) > 0;
        }


        return (returnValue);
    }

    public boolean deleteAccount(Context ctx, int id) {

        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(SETTINGS_TABLE, "id=" + id, null);
        } finally {

        }

        boolean returnValue = false;
        if (rowsAffected > 0) {
            returnValue = true;
        }

        // delete QuickPress homescreen shortcuts connected with this account
        List<Map<String, Object>> shortcuts = getQuickPressShortcuts(id);
        for (int i = 0; i < shortcuts.size(); i++) {
            Map<String, Object> shortcutHash = shortcuts.get(i);

            Intent shortcutIntent = new Intent();
            shortcutIntent.setClassName(EditPostActivity.class.getPackage().getName(),
                    EditPostActivity.class.getName());
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            shortcutIntent.setAction(Intent.ACTION_VIEW);
            Intent broadcastShortcutIntent = new Intent();
            broadcastShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
                    shortcutIntent);
            broadcastShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                    shortcutHash.get("name").toString());
            broadcastShortcutIntent.putExtra("duplicate", false);
            broadcastShortcutIntent
                    .setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            ctx.sendBroadcast(broadcastShortcutIntent);

            deleteQuickPressShortcut(shortcutHash.get("id").toString());
        }

        return (returnValue);
    }

    public List<Object> loadSettings(int id) {

        Cursor c = db.query(SETTINGS_TABLE, new String[] { "url", "blogName",
                "username", "password", "httpuser", "httppassword",
                "imagePlacement", "centerThumbnail", "fullSizeImage",
                "maxImageWidth", "maxImageWidthId", "runService", "blogId",
                "location", "dotcomFlag", "dotcom_username", "dotcom_password",
                "api_key", "api_blogid", "wpVersion", "postFormats",
                "lastCommentId","isScaledImage","scaledImgWidth", "homeURL", "blog_options" }, "id=" + id, null, null, null, null);

        int numRows = c.getCount();
        c.moveToFirst();

        List<Object> returnVector = new Vector<Object>();
        if (numRows > 0) {
            if (c.getString(0) != null) {
                returnVector.add(c.getString(0));
                returnVector.add(c.getString(1));
                returnVector.add(c.getString(2));
                returnVector.add(decryptPassword(c.getString(3)));
                if (c.getString(4) == null) {
                    returnVector.add("");
                } else {
                    returnVector.add(c.getString(4));
                }
                if (c.getString(5) == null) {
                    returnVector.add("");
                } else {
                    returnVector.add(decryptPassword(c.getString(5)));
                }
                returnVector.add(c.getString(6));
                returnVector.add(c.getInt(7));
                returnVector.add(c.getInt(8));
                returnVector.add(c.getString(9));
                returnVector.add(c.getInt(10));
                returnVector.add(c.getInt(11));
                returnVector.add(c.getInt(12));
                returnVector.add(c.getInt(13));
                returnVector.add(c.getInt(14));
                returnVector.add(c.getString(15));
                returnVector.add(decryptPassword(c.getString(16)));
                returnVector.add(c.getString(17));
                returnVector.add(c.getString(18));
                returnVector.add(c.getString(19));
                returnVector.add(c.getString(20));
                returnVector.add(c.getInt(21));
                returnVector.add(c.getInt(22));
                returnVector.add(c.getInt(23));
                returnVector.add(c.getString(24));
                returnVector.add(c.getString(25));
            } else {
                returnVector = null;
            }
        } else {
            returnVector = null;
        }
        c.close();

        return returnVector;
    }

    public List<String> loadStatsLogin(int id) {

        Cursor c = db.query(SETTINGS_TABLE, new String[] { "dotcom_username",
                "dotcom_password" }, "id=" + id, null, null, null, null);

        c.moveToFirst();

        List<String> returnVector = new Vector<String>();
        if (c.getString(0) != null) {
            returnVector.add(c.getString(0));
            returnVector.add(decryptPassword(c.getString(1)));
        } else {
            returnVector = null;
        }
        c.close();

        return returnVector;
    }

    public boolean updateLatestCommentID(int id, Integer newCommentID) {

        boolean returnValue = false;

        synchronized (this) {
            ContentValues values = new ContentValues();
            values.put("lastCommentId", newCommentID);

            returnValue = db.update(SETTINGS_TABLE, values, "id=" + id, null) > 0;
        }

        return (returnValue);

    }

    public List<Integer> getNotificationAccounts() {

        Cursor c = null;
        try {
            c = db.query(SETTINGS_TABLE, new String[] { "id" }, "runService=1",
                    null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int numRows = c.getCount();
        c.moveToFirst();

        List<Integer> returnVector = new Vector<Integer>();
        for (int i = 0; i < numRows; ++i) {
            int tempID = c.getInt(0);
            returnVector.add(tempID);
            c.moveToNext();
        }

        c.close();

        return returnVector;
    }

    public String getAccountName(String accountID) {

        String accountName = "";
        Cursor c = db.query(SETTINGS_TABLE, new String[] { "blogName" }, "id="
                + accountID, null, null, null, null);
        c.moveToFirst();
        if (c.getString(0) != null) {
            accountName = c.getString(0);
        }
        c.close();

        return accountName;
    }

    public void updateNotificationFlag(int id, boolean flag) {

        ContentValues values = new ContentValues();
        int iFlag = 0;
        if (flag) {
            iFlag = 1;
        }
        values.put("runService", iFlag);

        boolean returnValue = db.update(SETTINGS_TABLE, values,
                "id=" + String.valueOf(id), null) > 0;
        if (returnValue) {
        }

    }

    public void updateNotificationSettings(String interval, boolean sound,
            boolean vibrate, boolean light, boolean tagline_flag, String tagline) {

        ContentValues values = new ContentValues();
        values.put("interval", interval);
        values.put("sound", sound);
        values.put("vibrate", vibrate);
        values.put("light", light);
        values.put("tagline_flag", tagline_flag);
        values.put("tagline", tagline);

        boolean returnValue = db.update(EULA_TABLE, values, null, null) > 0;
        if (returnValue) {
        }
        ;

    }

    public String getInterval() {

        Cursor c = db.query("eula", new String[] { "interval" }, "id=0", null,
                null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();
        String returnValue = "";
        if (numRows == 1) {
            if (c.getString(0) != null) {
                returnValue = c.getString(0);
            }
        }
        c.close();

        return returnValue;

    }

    public Map<String, Object> getNotificationOptions() {

        Cursor c = db.query(EULA_TABLE, new String[] { "id", "sound", "vibrate",
                "light", "tagline_flag", "tagline" }, "id=0", null, null, null,
                null);
        int sound, vibrate, light;
        String tagline;
        Map<String, Object> thisHash = new HashMap<String, Object>();
        int numRows = c.getCount();
        if (numRows >= 1) {
            c.moveToFirst();

            sound = c.getInt(1);
            vibrate = c.getInt(2);
            light = c.getInt(3);
            tagline = c.getString(5);
            thisHash.put("sound", sound);
            thisHash.put("vibrate", vibrate);
            thisHash.put("light", light);
            thisHash.put("tagline_flag", c.getInt(4));
            if (tagline != null) {
                thisHash.put("tagline", tagline);
            } else {
                thisHash.put("tagline", "");
            }

        } else {
            return null;
        }

        c.close();

        return thisHash;
    }

    /**
     * Set the ID of the most recently active blog. This value will persist between application
     * launches.
     * 
     * @param id ID of the most recently active blog.
     */
    public void updateLastBlogId(int id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("last_blog_id", id);
        editor.commit();
    }

    /**
     * Delete the ID for the most recently active blog.
     */
    public void deleteLastBlogId() {
        updateLastBlogId(-1);
        // Clear the last selected activity
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("wp_pref_last_activity", -1);
        editor.commit();
    }

    /**
     * Get the ID of the most recently active blog. -1 is returned if there is no recently active
     * blog.
     */
    public int getLastBlogId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("last_blog_id", -1);
    }

    public List<Map<String, Object>> loadDrafts(int blogID,
            boolean loadPages) {

        List<Map<String, Object>> returnVector = new Vector<Map<String, Object>>();
        Cursor c;
        if (loadPages)
            c = db.query(POSTS_TABLE, new String[] { "id", "title",
                    "post_status", "uploaded", "date_created_gmt",
                    "post_status" }, "blogID=" + blogID
                    + " AND localDraft=1 AND uploaded=0 AND isPage=1", null,
                    null, null, null);
        else
            c = db.query(POSTS_TABLE, new String[] { "id", "title",
                    "post_status", "uploaded", "date_created_gmt",
                    "post_status" }, "blogID=" + blogID
                    + " AND localDraft=1 AND uploaded=0 AND isPage=0", null,
                    null, null, null);

        int numRows = c.getCount();
        c.moveToFirst();

        for (int i = 0; i < numRows; ++i) {
            if (c.getString(0) != null) {
                Map<String, Object> returnHash = new HashMap<String, Object>();
                returnHash.put("id", c.getString(0));
                returnHash.put("title", c.getString(1));
                returnHash.put("status", c.getString(2));
                returnHash.put("uploaded", c.getInt(3));
                returnHash.put("date_created_gmt", c.getLong(4));
                returnHash.put("post_status", c.getString(5));
                returnVector.add(i, returnHash);
            }
            c.moveToNext();
        }
        c.close();

        if (numRows == 0) {
            returnVector = null;
        }

        return returnVector;
    }

    public boolean deletePost(Post post) {

        boolean returnValue = false;

        int result = 0;
        result = db.delete(POSTS_TABLE, "blogID=" + post.getBlogID()
                + " AND id=" + post.getId(), null);

        if (result == 1) {
            returnValue = true;
        }

        return returnValue;
    }

    public boolean savePosts(List<?> postValues, int blogID, boolean isPage) {
        boolean returnValue = false;
        
        if (postValues.size() != 0) {
            for (int i = 0; i < postValues.size(); i++) {
                try {
                    ContentValues values = new ContentValues();
                    Map<?, ?> thisHash = (Map<?, ?>) postValues.get(i);
                    values.put("blogID", blogID);
                    if (thisHash.get((isPage) ? "page_id" : "postid") == null)
                        return false;
                    String postID = thisHash.get((isPage) ? "page_id" : "postid")
                            .toString();
                    values.put("postid", postID);
                    values.put("title", thisHash.get("title").toString());
                    Date d;
                    try {
                        d = (Date) thisHash.get("dateCreated");
                        values.put("dateCreated", d.getTime());
                    } catch (Exception e) {
                        Date now = new Date();
                        values.put("dateCreated", now.getTime());
                    }
                    try {
                        d = (Date) thisHash.get("date_created_gmt");
                        values.put("date_created_gmt", d.getTime());
                    } catch (Exception e) {
                        d = new Date((Long) values.get("dateCreated"));
                        values.put("date_created_gmt",
                                d.getTime() + (d.getTimezoneOffset() * 60000));
                    }
                    values.put("description", thisHash.get("description")
                            .toString());
                    values.put("link", thisHash.get("link").toString());
                    values.put("permaLink", thisHash.get("permaLink").toString());

                    Object[] cats = (Object[]) thisHash.get("categories");
                    JSONArray jsonArray = new JSONArray();
                    if (cats != null) {
                        for (int x = 0; x < cats.length; x++) {
                            jsonArray.put(cats[x].toString());
                        }
                    }
                    values.put("categories", jsonArray.toString());

                    Object[] custom_fields = (Object[]) thisHash
                            .get("custom_fields");
                    jsonArray = new JSONArray();
                    if (custom_fields != null) {
                        for (int x = 0; x < custom_fields.length; x++) {
                            jsonArray.put(custom_fields[x].toString());
                            // Update geo_long and geo_lat from custom fields, if
                            // found:
                           
                            Map<?, ?> customField = (Map<?, ?>) custom_fields[x];
                            if (customField.get("key") != null
                                    && customField.get("value") != null) {
                                if (customField.get("key").equals("geo_longitude"))
                                    values.put("longitude", customField
                                            .get("value").toString());
                                if (customField.get("key").equals("geo_latitude"))
                                    values.put("latitude", customField.get("value")
                                            .toString());
                                if (customField.get("key").equals("rbca_asser_name"))
                                    values.put("rbca_asser_name", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_asser_org"))
                                    values.put("rbca_asser_org", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_asser_email"))
                                    values.put("rbca_asser_email", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_asser_phone"))
                                    values.put("rbca_asser_phone", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_coord_latitude"))
                                    values.put("rbca_coord_latitude", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_coord_longitude"))
                                    values.put("rbca_coord_longitude", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_coord_altitude"))
                                    values.put("rbca_coord_altitude", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_coord_accuracy"))
                                    values.put("rbca_coord_accuracy", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_coord_loc"))
                                    values.put("rbca_coord_loc",(String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_coord_loc_oth"))
                                    values.put("rbca_coord_loc_oth",(String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_coord_corner"))
                                    values.put("rbca_coord_corner",(String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_coord_notes"))
                                    values.put("rbca_coord_notes",(String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_addr_no"))
                                    values.put("rbca_addr_no",(String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_addr_street"))
                                    values.put("rbca_addr_street",(String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_addr_notes"))
                                    values.put("rbca_addr_notes", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_img_right"))
                                    values.put("rbca_img_right", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_img_front"))
                                    values.put("rbca_img_front", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_img_left"))
                                    values.put("rbca_img_left", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_bldg_name"))
                                    values.put("rbca_bldg_name", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_bldg_is_extant"))
                                    values.put("rbca_bldg_is_extant", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_bldg_area")){
                                    values.put("rbca_bldg_area",(String) customField.get("value"));
                                }
                                if (customField.get("key").equals("rbca_bldg_posting"))
                                    values.put("rbca_bldg_posting",(String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_posting_oth"))
                                    values.put("rbca_bldg_posting_oth",(String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_posting_img"))
                                    values.put("rbca_bldg_posting_img", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_bldg_occucy"))
                                    values.put("rbca_bldg_occucy",(String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_occu_avail"))
                                    values.put("rbca_bldg_occucy_avail", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_stories"))
                                    values.put("rbca_bldg_stories", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_width"))
                                    values.put("rbca_bldg_width", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_length"))
                                    values.put("rbca_bldg_length", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_use"))
                                    values.put("rbca_bldg_use", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_use_oth"))
                                    values.put("rbca_bldg_use_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_outbldg"))
                                    values.put("rbca_bldg_outbldg", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_outbldg_notes"))
                                    values.put("rbca_bldg_outbldg_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_units_res"))
                                    values.put("rbca_bldg_units_res", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_units_comm"))
                                    values.put("rbca_bldg_units_comm", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_occu_name"))
                                    values.put("rbca_bldg_occu_name", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_occu_phone"))
                                    values.put("rbca_bldg_occu_phone", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_bldg_notes"))
                                    values.put("rbca_bldg_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_desig"))
                                    values.put("rbca_hist_desig", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_desig_oth"))
                                    values.put("rbca_hist_desig_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_dist"))
                                    values.put("rbca_hist_dist", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_dist_name"))
                                    values.put("rbca_hist_dist_name", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_appear"))
                                    values.put("rbca_hist_appear", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_age"))
                                    values.put("rbca_hist_age", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_age_meta"))
                                    values.put("rbca_hist_age_meta", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_yr_built"))
                                    values.put("rbca_hist_yr_built", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_age_src"))
                                    values.put("rbca_hist_age_src", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_age_src_oth"))
                                    values.put("rbca_hist_age_src_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hist_notes"))
                                    values.put("rbca_hist_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_dmg_source"))
                                    values.put("rbca_dmg_source", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_dmg_source_oth"))
                                    values.put("rbca_dmg_source_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_dmg_total"))
                                    values.put("rbca_dmg_total", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_dmg_desc"))
                                    values.put("rbca_dmg_desc", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_struct_type"))
                                    values.put("rbca_struct_type", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_struct_type_oth"))
                                    values.put("rbca_struct_type_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_struc_defects"))
                                    values.put("rbca_struc_defects", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_struct"))
                                    values.put("rbca_struct", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_struct_notes"))
                                    values.put("rbca_struct_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_found_type"))
                                    values.put("rbca_found_type", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_found_type_oth"))
                                    values.put("rbca_found_type_oth", (String) customField.get("value"));

                                if (customField.get("key").equals("rbca_found"))
                                    values.put("rbca_found", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_found_notes"))
                                    values.put("rbca_found_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_extwall_mat"))
                                    values.put("rbca_extwall_mat", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_extwall_mat_oth"))
                                    values.put("rbca_extwall_mat_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_extwall"))
                                    values.put("rbca_extwall", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_extwall_notes"))
                                    values.put("rbca_extwall_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_extfeat_type"))
                                    values.put("rbca_extfeat_type", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_extfeat_type_oth"))
                                    values.put("rbca_extfeat_type_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_extfeat"))
                                    values.put("rbca_extfeat", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_extfeat_notes"))
                                    values.put("rbca_extfeat_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_win_type"))
                                    values.put("rbca_win_type", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_win_type_oth"))
                                    values.put("rbca_win_type_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_win_mat"))
                                    values.put("rbca_win_mat", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_win_mat_oth"))
                                    values.put("rbca_win_mat_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_win"))
                                    values.put("rbca_win", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_win_notes"))
                                    values.put("rbca_win_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_roof_type"))
                                    values.put("rbca_roof_type", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_roof_type_oth"))
                                    values.put("rbca_roof_type_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_roof_mat"))
                                    values.put("rbca_roof_mat", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_roof_mat_oth"))
                                    values.put("rbca_roof_mat_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_roof"))
                                    values.put("rbca_roof", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_roof_notes"))
                                    values.put("rbca_roof_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_int_cond"))
                                    values.put("rbca_int_cond", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_int_collect_extant"))
                                    values.put("rbca_int_collect_extant", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_int_collect_type"))
                                    values.put("rbca_int_collect_type", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_int_collect_type_oth"))
                                    values.put("rbca_int_collect_type_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_int_img1"))
                                    values.put("rbca_int_img1", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_int_desc1"))
                                    values.put("rbca_int_desc1", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_int_img2"))
                                    values.put("rbca_int_img2", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_int_desc2"))
                                    values.put("rbca_int_desc2", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_int_img3"))
                                    values.put("rbca_int_img3", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_int_desc3"))
                                    values.put("rbca_int_desc3", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_int_notes"))
                                    values.put("rbca_int_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_landveg_feat"))
                                    values.put("rbca_landveg_feat", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_landveg_feat_oth"))
                                    values.put("rbca_landveg_feat_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_landveg"))
                                    values.put("rbca_landveg", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_landveg_notes"))
                                    values.put("rbca_landveg_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_landblt_feat"))
                                    values.put("rbca_landblt_feat", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_landblt_feat_oth"))
                                    values.put("rbca_landblt_feat_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_landblt"))
                                    values.put("rbca_landblt", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_landblt_notes"))
                                    values.put("rbca_landblt_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_media_img1"))
                                    values.put("rbca_media_img1", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_desc1"))
                                    values.put("rbca_media_desc1", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_img2"))
                                    values.put("rbca_media_img2", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_desc2"))
                                    values.put("rbca_media_desc2", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_img3"))
                                    values.put("rbca_media_img3", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_desc3"))
                                    values.put("rbca_media_desc3", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_img4"))
                                    values.put("rbca_media_img4", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_desc4"))
                                    values.put("rbca_media_desc4", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_img5"))
                                    values.put("rbca_media_img5", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_desc5"))
                                    values.put("rbca_media_desc5", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_img6"))
                                    values.put("rbca_media_img6", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_media_desc6"))
                                    values.put("rbca_media_desc6", (String) customField.get("value").toString());
                                
                                if (customField.get("key").equals("rbca_hzrd"))
                                    values.put("rbca_hzrd", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hzrd_type"))
                                    values.put("rbca_hzrd_type", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hzrd_type_oth"))
                                    values.put("rbca_hzrd_type_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hzrd_notes"))
                                    values.put("rbca_hzrd_notes", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hzrd_hazmat"))
                                    values.put("rbca_hzrd_hazmat", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_hzrd_hazmat_oth"))
                                    values.put("rbca_hzrd_hazmat_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_actn"))
                                    values.put("rbca_actn", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_actn_oth"))
                                    values.put("rbca_actn_oth", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_eval"))
                                    values.put("rbca_eval", (String) customField.get("value"));
                                
                                if (customField.get("key").equals("rbca_eval_oth"))
                                    values.put("rbca_eval_oth", (String) customField.get("value"));
                            }
                        }
                    }
                    values.put("custom_fields", jsonArray.toString());

                    values.put("mt_excerpt",
                            thisHash.get((isPage) ? "excerpt" : "mt_excerpt")
                                    .toString());
                    values.put("mt_text_more",
                            thisHash.get((isPage) ? "text_more" : "mt_text_more")
                                    .toString());
                    values.put("mt_allow_comments",
                            (Integer) thisHash.get("mt_allow_comments"));
                    values.put("mt_allow_pings",
                            (Integer) thisHash.get("mt_allow_pings"));
                    values.put("wp_slug", thisHash.get("wp_slug").toString());
                    values.put("wp_password", thisHash.get("wp_password")
                            .toString());
                    values.put("wp_author_id", thisHash.get("wp_author_id")
                            .toString());
                    values.put("wp_author_display_name",
                            thisHash.get("wp_author_display_name").toString());
                    values.put("post_status",
                            thisHash.get((isPage) ? "page_status" : "post_status")
                                    .toString());
                    values.put("userid", thisHash.get("userid").toString());

                    int isPageInt = 0;
                    if (isPage) {
                        isPageInt = 1;
                        values.put("isPage", true);
                        values.put("wp_page_parent_id",
                                thisHash.get("wp_page_parent_id").toString());
                        values.put("wp_page_parent_title",
                                thisHash.get("wp_page_parent_title").toString());
                    } else {
                        values.put("mt_keywords", thisHash.get("mt_keywords")
                                .toString());
                        try {
                            values.put("wp_post_format",
                                    thisHash.get("wp_post_format").toString());
                        } catch (Exception e) {
                            values.put("wp_post_format", "");
                        }
                    }


                    int result = db.update(POSTS_TABLE, values, "postID=" + postID
                            + " AND isPage=" + isPageInt, null);
                    if (result == 0)
                        returnValue = db.insert(POSTS_TABLE, null, values) > 0;
                    else
                        returnValue = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return (returnValue);
    }

    public long savePost(Post post, int blogID) {
        long returnValue = -1;
        if (post != null) {

            ContentValues values = new ContentValues();
            values.put("blogID", blogID);
            values.put("title", post.getTitle());
            values.put("date_created_gmt", post.getDate_created_gmt());
            values.put("description", post.getDescription());
            values.put("mt_text_more", post.getMt_text_more());

            if (post.getCategories() != null) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(post.getCategories().toString());
                    values.put("categories", jsonArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            values.put("localDraft", post.isLocalDraft());
            values.put("mediaPaths", post.getMediaPaths());
            values.put("mt_keywords", post.getMt_keywords());
            values.put("wp_password", post.getWP_password());
            values.put("post_status", post.getPost_status());
            values.put("uploaded", post.isUploaded());
            values.put("isPage", post.isPage());
            values.put("wp_post_format", post.getWP_post_format());
            values.put("latitude", post.getLatitude());
            values.put("longitude", post.getLongitude());
            values.put("isLocalChange", post.isLocalChange());
            
            ///added MoCA for Android
            values.put("rbca_asser_name", post.getRBCA_asser_name());
            values.put("rbca_asser_org", post.getRBCA_asser_org());
            values.put("rbca_asser_email", post.getRBCA_asser_email());
            values.put("rbca_asser_phone", post.getRBCA_asser_phone());
            values.put("rbca_coord_latitude", post.getRBCA_coord_latitude());
            values.put("rbca_coord_longitude", post.getRBCA_coord_longitude());
            values.put("rbca_coord_altitude", post.getRBCA_coord_altitude());
            values.put("rbca_coord_accuracy", post.getRBCA_coord_accuracy());
            values.put("rbca_coord_loc", post.getRBCA_coord_loc());
            values.put("rbca_coord_loc_oth", post.getRBCA_coord_loc_oth());
            values.put("rbca_coord_corner" , post.getRBCA_coord_corner());
            values.put("rbca_coord_notes",post.getRBCA_coord_notes());
            values.put("rbca_addr_no", post.getRBCA_addr_no());
            values.put("rbca_addr_street", post.getRBCA_addr_street());
            values.put("rbca_addr_notes", post.getRBCA_addr_notes());
            values.put("rbca_img_right", post.getRBCA_img_right());
            values.put("rbca_img_front", post.getRBCA_img_front());
            values.put("rbca_img_left", post.getRBCA_img_left());
            values.put("rbca_bldg_name", post.getRBCA_bldg_name());
            values.put("rbca_bldg_is_extant", post.getRBCA_bldg_is_extant());
            values.put("rbca_bldg_area", post.getRBCA_bldg_area());
            values.put("rbca_bldg_posting", post.getRBCA_bldg_posting());
            values.put("rbca_bldg_posting_oth", post.getRBCA_bldg_posting_oth());
            values.put("rbca_bldg_posting_img", post.getRBCA_bldg_posting_img());
            values.put("rbca_bldg_occucy", post.getRBCA_bldg_occucy());
            values.put("rbca_bldg_occucy_avail", post.getRBCA_bldg_occucy_avail());
            values.put("rbca_bldg_stories", post.getRBCA_bldg_stories());
            values.put("rbca_bldg_width", post.getRBCA_bldg_width());
            values.put("rbca_bldg_length", post.getRBCA_bldg_length());
            values.put("rbca_bldg_use", post.getRBCA_bldg_use());
            values.put("rbca_bldg_use_oth", post.getRBCA_bldg_use_oth());
            values.put("rbca_bldg_outbldg", post.getRBCA_bldg_outbldg());
            values.put("rbca_bldg_outbldg_notes", post.getRBCA_bldg_outbldg_notes());
            values.put("rbca_bldg_units_res", post.getRBCA_bldg_units_res());
            values.put("rbca_bldg_units_comm", post.getRBCA_bldg_units_comm());
            values.put("rbca_bldg_occu_name", post.getRBCA_bldg_occu_name());
            values.put("rbca_bldg_occu_phone" , post.getRBCA_bldg_occu_phone());
            values.put("rbca_bldg_notes", post.getRBCA_bldg_notes());
            values.put("rbca_hist_desig", post.getRBCA_hist_desig());
            values.put("rbca_hist_desig_oth", post.getRBCA_hist_desig_oth());
            values.put("rbca_hist_dist", post.getRBCA_hist_dist());
            values.put("rbca_hist_dist_name", post.getRBCA_hist_dist_name());
            values.put("rbca_hist_appear", post.getRBCA_hist_appear());
            values.put("rbca_hist_age", post.getRBCA_hist_age());
            values.put("rbca_hist_age_meta", post.getRBCA_hist_age_meta());
            values.put("rbca_hist_yr_built", post.getRBCA_hist_yr_built());
            values.put("rbca_hist_age_src", post.getRBCA_hist_age_src());
            values.put("rbca_hist_age_src_oth", post.getRBCA_hist_age_src_oth());
            values.put("rbca_hist_notes", post.getRBCA_hist_notes());
            values.put("rbca_dmg_source", post.getRBCA_dmg_source());
            values.put("rbca_dmg_source_oth", post.getRBCA_dmg_source_oth());
            values.put("rbca_dmg_total", post.getRBCA_dmg_total());
            values.put("rbca_dmg_desc", post.getRBCA_dmg_desc());
            values.put("rbca_struct_type", post.getRBCA_struct_type());
            values.put("rbca_struct_type_oth", post.getRBCA_struct_type_oth());
            values.put("rbca_struct_defects", post.getRBCA_struct_defects());
            values.put("rbca_struct", post.getRBCA_struct());
            values.put("rbca_struct_notes", post.getRBCA_struct_notes());
            values.put("rbca_found_type", post.getRBCA_found_type());
            values.put("rbca_found_type_oth", post.getRBCA_found_type_oth());
            values.put("rbca_found", post.getRBCA_found());
            values.put("rbca_found_notes", post.getRBCA_found_notes());
            values.put("rbca_extwall_mat", post.getRBCA_extwall_mat());
            values.put("rbca_extwall_mat_oth", post.getRBCA_extwall_mat_oth());
            values.put("rbca_extwall", post.getRBCA_extwall());
            values.put("rbca_extwall_notes", post.getRBCA_extwall_notes());
            values.put("rbca_extfeat_type", post.getRBCA_extfeat_type());
            values.put("rbca_extfeat_type_oth", post.getRBCA_extfeat_type_oth());
            values.put("rbca_extfeat", post.getRBCA_extfeat());
            values.put("rbca_extfeat_notes", post.getRBCA_extfeat_notes());
            values.put("rbca_win_type", post.getRBCA_win_type());
            values.put("rbca_win_type_oth", post.getRBCA_win_type_oth());
            values.put("rbca_win_mat", post.getRBCA_win_mat());
            values.put("rbca_win_mat_oth", post.getRBCA_win_mat_oth());
            values.put("rbca_win", post.getRBCA_win());
            values.put("rbca_win_notes", post.getRBCA_win_notes());
            values.put("rbca_roof_type", post.getRBCA_roof_type());
            values.put("rbca_roof_type_oth", post.getRBCA_roof_type_oth());
            values.put("rbca_roof_mat", post.getRBCA_roof_mat());
            values.put("rbca_roof_mat_oth", post.getRBCA_roof_mat_oth());
            values.put("rbca_roof", post.getRBCA_roof());
            values.put("rbca_roof_notes", post.getRBCA_roof_notes());
            values.put("rbca_int_cond", post.getRBCA_int_cond());
            values.put("rbca_int_collect_extant", post.getRBCA_int_collect_extant());
            values.put("rbca_int_collect_type", post.getRBCA_int_collect_type());
            values.put("rbca_int_collect_type_oth", post.getRBCA_int_collect_type_oth());
            values.put("rbca_int_img1", post.getRBCA_int_img1());
            values.put("rbca_int_desc1", post.getRBCA_int_desc1());
            values.put("rbca_int_img2", post.getRBCA_int_img2());
            values.put("rbca_int_desc2", post.getRBCA_int_desc2());
            values.put("rbca_int_img3", post.getRBCA_int_img3());
            values.put("rbca_int_desc3", post.getRBCA_int_desc3());
            values.put("rbca_int_notes", post.getRBCA_int_notes());
            values.put("rbca_landveg_feat", post.getRBCA_landveg_feat());
            values.put("rbca_landveg_feat_oth", post.getRBCA_landveg_feat_oth());
            values.put("rbca_landveg", post.getRBCA_landveg());
            values.put("rbca_landveg_notes", post.getRBCA_landveg_notes());
            values.put("rbca_landblt_feat", post.getRBCA_landblt_feat());
            values.put("rbca_landblt_feat_oth", post.getRBCA_landblt_feat_oth());
            values.put("rbca_landblt", post.getRBCA_landblt());
            values.put("rbca_landblt_notes", post.getRBCA_landblt_notes());
            values.put("rbca_media_img1", post.getRBCA_media_img1());
            values.put("rbca_media_desc1", post.getRBCA_media_desc1());
            values.put("rbca_media_img2", post.getRBCA_media_img2());
            values.put("rbca_media_desc2", post.getRBCA_media_desc2());
            values.put("rbca_media_img3", post.getRBCA_media_img3());
            values.put("rbca_media_desc3", post.getRBCA_media_desc3());
            values.put("rbca_media_img4", post.getRBCA_media_img4());
            values.put("rbca_media_desc4", post.getRBCA_media_desc4());
            values.put("rbca_media_img5", post.getRBCA_media_img5());
            values.put("rbca_media_desc5", post.getRBCA_media_desc5());
            values.put("rbca_media_img6", post.getRBCA_media_img6());
            values.put("rbca_media_desc6", post.getRBCA_media_desc6());
            values.put("rbca_hzrd", post.getRBCA_hzrd());
            values.put("rbca_hzrd_type", post.getRBCA_hzrd_type());
            values.put("rbca_hzrd_type_oth", post.getRBCA_hzrd_type_oth());
            values.put("rbca_hzrd_notes", post.getRBCA_hzrd_notes());
            values.put("rbca_hzrd_hazmat", post.getRBCA_hzrd_hazmat());
            values.put("rbca_hzrd_hazmat_oth", post.getRBCA_hzrd_hazmat_oth());
            values.put("rbca_actn", post.getRBCA_actn());
            values.put("rbca_actn_oth", post.getRBCA_actn_oth());
            values.put("rbca_eval", post.getRBCA_eval());
            values.put("rbca_eval_oth", post.getRBCA_eval_oth());
            
            
            ///end added MoCA for Android

            returnValue = db.insert(POSTS_TABLE, null, values);
        }
        return (returnValue);
    }

    public int updatePost(Post post, int blogID) {
        int success = 0;
        if (post != null) {

            ContentValues values = new ContentValues();
            values.put("blogID", blogID);
            values.put("title", post.getTitle());
            values.put("date_created_gmt", post.getDate_created_gmt());
            values.put("description", post.getDescription());
            if (post.getMt_text_more() != null)
                values.put("mt_text_more", post.getMt_text_more());
            values.put("uploaded", post.isUploaded());

            if (post.getCategories() != null) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(post.getCategories().toString());
                    values.put("categories", jsonArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            values.put("localDraft", post.isLocalDraft());
            values.put("mediaPaths", post.getMediaPaths());
            values.put("mt_keywords", post.getMt_keywords());
            values.put("wp_password", post.getWP_password());
            values.put("post_status", post.getPost_status());
           
            values.put("isPage", post.isPage());
            values.put("wp_post_format", post.getWP_post_format());
            
            values.put("isLocalChange", post.isLocalChange());
            
            ////////////////////////added Surveys for Android
            values.put("rbca_coord_loc", post.getRBCA_coord_loc());
            values.put("rbca_coord_loc_oth", post.getRBCA_coord_loc_oth());
            values.put("rbca_coord_corner" , post.getRBCA_coord_corner());
            values.put("rbca_coord_notes",post.getRBCA_coord_notes());
            values.put("rbca_addr_no", post.getRBCA_addr_no());
            values.put("rbca_addr_street", post.getRBCA_addr_street());
            values.put("rbca_bldg_area", post.getRBCA_bldg_area());
            values.put("rbca_bldg_posting", post.getRBCA_bldg_posting());
            values.put("rbca_bldg_posting_oth", post.getRBCA_bldg_posting_oth());
            values.put("rbca_bldg_occucy", post.getRBCA_bldg_occucy());
            values.put("rbca_bldg_occucy_avail", post.getRBCA_bldg_occucy_avail());
            values.put("rbca_bldg_stories", post.getRBCA_bldg_stories());
            values.put("rbca_bldg_width", post.getRBCA_bldg_width());
            values.put("rbca_bldg_length", post.getRBCA_bldg_length());
            values.put("rbca_bldg_use", post.getRBCA_bldg_use());
            values.put("rbca_bldg_use_oth", post.getRBCA_bldg_use_oth());
            values.put("rbca_bldg_outbldg", post.getRBCA_bldg_outbldg());
            values.put("rbca_bldg_outbldg_notes", post.getRBCA_bldg_outbldg_notes());
            values.put("rbca_bldg_units_res", post.getRBCA_bldg_units_res());
            values.put("rbca_bldg_units_comm", post.getRBCA_bldg_units_comm());
            values.put("rbca_bldg_occu_name", post.getRBCA_bldg_occu_name());
            values.put("rbca_bldg_occu_phone" , post.getRBCA_bldg_occu_phone());
            values.put("rbca_bldg_notes", post.getRBCA_bldg_notes());
            values.put("rbca_hist_desig", post.getRBCA_hist_desig());
            values.put("rbca_hist_desig_oth", post.getRBCA_hist_desig_oth());
            values.put("rbca_hist_dist", post.getRBCA_hist_dist());
            values.put("rbca_hist_dist_name", post.getRBCA_hist_dist_name());
            values.put("rbca_hist_appear", post.getRBCA_hist_appear());
            values.put("rbca_hist_age", post.getRBCA_hist_age());
            values.put("rbca_hist_age_meta", post.getRBCA_hist_age_meta());
            values.put("rbca_hist_yr_built", post.getRBCA_hist_yr_built());
            values.put("rbca_hist_age_src", post.getRBCA_hist_age_src());
            values.put("rbca_hist_age_src_oth", post.getRBCA_hist_age_src_oth());
            values.put("rbca_hist_notes", post.getRBCA_hist_notes());
            values.put("rbca_dmg_source", post.getRBCA_dmg_source());
            values.put("rbca_dmg_source_oth", post.getRBCA_dmg_source_oth());
            values.put("rbca_dmg_total", post.getRBCA_dmg_total());
            values.put("rbca_dmg_desc", post.getRBCA_dmg_desc());
            values.put("rbca_struct_type", post.getRBCA_struct_type());
            values.put("rbca_struct_type_oth", post.getRBCA_struct_type_oth());
            values.put("rbca_struct_defects", post.getRBCA_struct_defects());
            values.put("rbca_struct", post.getRBCA_struct());
            values.put("rbca_struct_notes", post.getRBCA_struct_notes());
            values.put("rbca_found_type", post.getRBCA_found_type());
            values.put("rbca_found_type_oth", post.getRBCA_found_type_oth());
            values.put("rbca_found", post.getRBCA_found());
            values.put("rbca_found_notes", post.getRBCA_found_notes());
            values.put("rbca_extwall_mat", post.getRBCA_extwall_mat());
            values.put("rbca_extwall_mat_oth", post.getRBCA_extwall_mat_oth());
            values.put("rbca_extwall", post.getRBCA_extwall());
            values.put("rbca_extwall_notes", post.getRBCA_extwall_notes());
            values.put("rbca_extfeat_type", post.getRBCA_extfeat_type());
            values.put("rbca_extfeat_type_oth", post.getRBCA_extfeat_type_oth());
            values.put("rbca_extfeat", post.getRBCA_extfeat());
            values.put("rbca_extfeat_notes", post.getRBCA_extfeat_notes());
            values.put("rbca_win_type", post.getRBCA_win_type());
            values.put("rbca_win_type_oth", post.getRBCA_win_type_oth());
            values.put("rbca_win_mat", post.getRBCA_win_mat());
            values.put("rbca_win_mat_oth", post.getRBCA_win_mat_oth());
            values.put("rbca_win", post.getRBCA_win());
            values.put("rbca_win_notes", post.getRBCA_win_notes());
            values.put("rbca_roof_type", post.getRBCA_roof_type());
            values.put("rbca_roof_type_oth", post.getRBCA_roof_type_oth());
            values.put("rbca_roof_mat", post.getRBCA_roof_mat());
            values.put("rbca_roof_mat_oth", post.getRBCA_roof_mat_oth());
            values.put("rbca_roof", post.getRBCA_roof());
            values.put("rbca_roof_notes", post.getRBCA_roof_notes());
            values.put("rbca_int_cond", post.getRBCA_int_cond());
            values.put("rbca_int_collect_extant", post.getRBCA_int_collect_extant());
            values.put("rbca_int_collect_type", post.getRBCA_int_collect_type());
            values.put("rbca_int_collect_type_oth", post.getRBCA_int_collect_type_oth());
            values.put("rbca_int_notes", post.getRBCA_int_notes());
            values.put("rbca_landveg_feat", post.getRBCA_landveg_feat());
            values.put("rbca_landveg_feat_oth", post.getRBCA_landveg_feat_oth());
            values.put("rbca_landveg", post.getRBCA_landveg());
            values.put("rbca_landveg_notes", post.getRBCA_landveg_notes());
            values.put("rbca_landblt_feat", post.getRBCA_landblt_feat());
            values.put("rbca_landblt_feat_oth", post.getRBCA_landblt_feat_oth());
            values.put("rbca_landblt", post.getRBCA_landblt());
            values.put("rbca_landblt_notes", post.getRBCA_landblt_notes());
            values.put("rbca_hzrd", post.getRBCA_hzrd());
            values.put("rbca_hzrd_type", post.getRBCA_hzrd_type());
            values.put("rbca_hzrd_type_oth", post.getRBCA_hzrd_type_oth());
            values.put("rbca_hzrd_notes", post.getRBCA_hzrd_notes());
            values.put("rbca_hzrd_hazmat", post.getRBCA_hzrd_hazmat());
            values.put("rbca_hzrd_hazmat_oth", post.getRBCA_hzrd_hazmat_oth());
            values.put("rbca_actn", post.getRBCA_actn());
            values.put("rbca_actn_oth", post.getRBCA_actn_oth());
            values.put("rbca_eval", post.getRBCA_eval());
            values.put("rbca_eval_oth", post.getRBCA_eval_oth());
            
            int pageInt = 0;
            if (post.isPage())
                pageInt = 1;

            success = db.update(POSTS_TABLE, values,
                    "blogID=" + post.getBlogID() + " AND id=" + post.getId()
                            + " AND isPage=" + pageInt, null);

        }
        return (success);
    }

    public List<Map<String, Object>> loadUploadedPosts(int blogID, boolean loadPages) {

        List<Map<String, Object>> returnVector = new Vector<Map<String, Object>>();
        Cursor c;
        if (loadPages)
            c = db.query(POSTS_TABLE,
                    new String[] { "id", "blogID", "postid", "title",
                            "date_created_gmt", "dateCreated", "post_status" },
                    "blogID=" + blogID + " AND localDraft != 1 AND isPage=1",
                    null, null, null, null);
        else
            c = db.query(POSTS_TABLE,
                    new String[] { "id", "blogID", "postid", "title",
                            "date_created_gmt", "dateCreated", "post_status" },
                    "blogID=" + blogID + " AND localDraft != 1 AND isPage=0",
                    null, null, null, null);

        int numRows = c.getCount();
        c.moveToFirst();

        for (int i = 0; i < numRows; ++i) {
            if (c.getString(0) != null) {
                Map<String, Object> returnHash = new HashMap<String, Object>();
                returnHash.put("id", c.getInt(0));
                returnHash.put("blogID", c.getString(1));
                returnHash.put("postID", c.getString(2));
                returnHash.put("title", c.getString(3));
                returnHash.put("date_created_gmt", c.getLong(4));
                returnHash.put("dateCreated", c.getLong(5));
                returnHash.put("post_status", c.getString(6));
                returnVector.add(i, returnHash);
            }
            c.moveToNext();
        }
        c.close();

        if (numRows == 0) {
            returnVector = null;
        }

        return returnVector;
    }

    public void deleteUploadedPosts(int blogID, boolean isPage) {

        if (isPage)
            db.delete(POSTS_TABLE, "blogID=" + blogID
                    + " AND localDraft != 1 AND isPage=1", null);
        else
            db.delete(POSTS_TABLE, "blogID=" + blogID
                    + " AND localDraft != 1 AND isPage=0", null);

    }

    
	public List<Object> loadPost(int blogID, boolean isPage, long id) {
        List<Object> values = null;

        int pageInt = 0;
        if (isPage)
            pageInt = 1;
        Cursor c = db.query(POSTS_TABLE, null, "blogID=" + blogID + " AND id="
                + id + " AND isPage=" + pageInt,null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            System.out.println("Numero de Columnas :" + c.getColumnCount());
            String[] columnlist = c.getColumnNames();
            
            for (int i= 0;i<c.getColumnCount();i++){
                System.out.println(i +": "+ columnlist[i] +" -> "+ c.getString(i));
            }
            
            if (c.getString(0) != null) {
                values = new Vector<Object>();
                values.add(c.getLong(0));
                values.add(c.getString(1));
                values.add(c.getString(2));
                values.add(c.getString(3));
                values.add(c.getLong(4));
                values.add(c.getLong(5));
                values.add(c.getString(6));
                values.add(c.getString(7));
                values.add(c.getString(8));
                values.add(c.getString(9));
                values.add(c.getInt(10));
                values.add(c.getInt(11));
                values.add(c.getString(12));
                values.add(c.getString(13));
                values.add(c.getString(14));
                values.add(c.getString(15));
                values.add(c.getString(16));
                values.add(c.getString(17));
                values.add(c.getString(18));
                values.add(c.getString(19));
                values.add(c.getString(20));
                values.add(c.getString(21));
                values.add(c.getString(22));
                values.add(c.getString(23));
                values.add(c.getDouble(24));
                values.add(c.getDouble(25));
                values.add(c.getInt(26));
                values.add(c.getInt(27));
                values.add(c.getInt(28));
                values.add(c.getString(31));  //rbca_coord_loc
                values.add(c.getString(32));  //rbca_coord_loc_oth
                values.add(c.getString(33));  //rbca_coord_corner
                values.add(c.getString(34));  //rbca_coord_notes
                values.add(c.getString(35));  //rbca_addr_no
                values.add(c.getString(36));  //rbca_addr_street
                values.add(c.getString(37));  //rbca_bldg_area
                values.add(c.getString(38));  //rbca_bldg_posting
                values.add(c.getString(39));  //rbca_bldg_posting_oth
                values.add(c.getString(40));  //rbca_bldg_occucy
                values.add(c.getInt(41));     //rbca_bldg_occucy_avail
                values.add(c.getDouble(42));  //rbca_bldg_stories
                values.add(c.getDouble(43));  //rbca_bldg_width
                values.add(c.getDouble(44));  //rbca_bldg_length
                values.add(c.getString(45));  //rbca_bldg_use
                values.add(c.getString(46));  //rbca_bldg_use_oth
                values.add(c.getInt(47));     //rbca_bldg_outbldg
                values.add(c.getString(48));  //rbca_bldg_outbldg_notes
                values.add(c.getInt(49));     //rbca_bldg_units_res
                values.add(c.getInt(50));     //rbca_bldg_units_comm 
                values.add(c.getString(51));  //rbca_bldg_occu_name
                values.add(c.getInt(52));     //rbca_bldg_occu_phone
                values.add(c.getString(53));  //rbca_bldg_notes
                values.add(c.getString(54));  //rbca_hist_desig
                values.add(c.getString(55));  //rbca_hist_desig_oth
                values.add(c.getString(56));  //rbca_hist_dist
                values.add(c.getString(57));  //rbca_hist_dist_name
                values.add(c.getInt(58));     //rbca_hist_appear
                values.add(c.getInt(59));     //rbca_hist_age
                values.add(c.getString(60));  //rbca_hist_age_meta
                values.add(c.getInt(61));     //rbca_hist_yr_built
                values.add(c.getInt(62));     //rbca_hist_age_src
                values.add(c.getString(63));  //rbca_hist_age_src_oth
                values.add(c.getString(64));  //rbca_hist_notes
                values.add(c.getString(65));  //rbca_dmg_source
                values.add(c.getString(66));  //rbca_dmg_source_oth
                values.add(c.getInt(67));     //rbca_dmg_total
                values.add(c.getString(68));  //rbca_dmg_desc
                values.add(c.getString(69));  //rbca_struct_type
                values.add(c.getString(70));  //rbca_struct_type_oth
                values.add(c.getString(71));  //rbca_struct_defects
                values.add(c.getInt(72));     //rbca_struct
                values.add(c.getString(73));  //rbca_struct_notes
                values.add(c.getString(74));  //rbca_found_type
                values.add(c.getString(75));  //rbca_found_type_oth
                values.add(c.getInt(76));     //rbca_found
                values.add(c.getInt(77));     //rbca_found_notes
                values.add(c.getString(78));  //rbca_extwall_mat
                values.add(c.getString(79));  //rbca_extwall_mat_oth
                values.add(c.getInt(80));     //rbca_extwall
                values.add(c.getString(81));  //rbca_extwall_notes
                values.add(c.getString(82));  //rbca_extfeat_type
                values.add(c.getString(83));  //rbca_extfeat_type_oth
                values.add(c.getInt(84));     //rbca_extfeat
                values.add(c.getString(85));  //rbca_extfeat_notes
                values.add(c.getString(86));  //rbca_win_type
                values.add(c.getString(87));  //rbca_win_type_oth
                values.add(c.getString(88));  //rbca_win_mat
                values.add(c.getString(89));  //rbca_win_mat_oth
                values.add(c.getInt(90));     //rbca_win
                values.add(c.getString(91));  //rbca_win_notes
                values.add(c.getString(92));  //rbca_roof_type
                values.add(c.getString(93));  //rbca_roof_type_oth
                values.add(c.getString(94));  //rbca_roof_mat
                values.add(c.getString(95));  //rbca_roof_mat_oth
                values.add(c.getInt(96));     //rbca_roof
                values.add(c.getString(97));  //rbca_roof_notes
                values.add(c.getString(98));  //rbca_int_cond
                values.add(c.getInt(99));     //rbca_int_collect_extant
                values.add(c.getString(100)); //rbca_int_collect_type
                values.add(c.getString(101)); //rbca_int_collect_type_oth
                values.add(c.getString(102)); //rbca_int_notes
                values.add(c.getString(103)); //rbca_landveg_feat
                values.add(c.getString(104)); //rbca_landveg_feat_oth
                values.add(c.getInt(105));    //rbca_landveg
                values.add(c.getString(106)); //rbca_landveg_notes
                values.add(c.getString(107)); //rbca_landblt_feat
                values.add(c.getString(108)); //rbca_landblt_feat_oth
                values.add(c.getInt(109));    //rbca_landblt
                values.add(c.getString(110)); //rbca_landblt_notes
                values.add(c.getInt(111));    //rbca_hzrd
                values.add(c.getString(112)); //rbca_hzrd_type
                values.add(c.getString(113)); //rbca_hzrd_type_oth
                values.add(c.getString(114)); //rbca_hzrd_notes
                values.add(c.getString(115)); //rbca_hzrd_hazmat
                values.add(c.getString(116)); //rbca_hzrd_hazmat_oth
                values.add(c.getString(117)); //rbca_actn
                values.add(c.getString(118)); //rbca_actn_oth
                values.add(c.getString(119)); //rbca_eval
                values.add(c.getString(120)); //rbca_eval_oth
                values.add(c.getInt(75));     //isLocalChange
                
                
            }
        }
        c.close();

        return values;
    }

    public List<Map<String, Object>> loadComments(int blogID) {

        List<Map<String, Object>> returnVector = new Vector<Map<String, Object>>();
        Cursor c = db.query(COMMENTS_TABLE,
                new String[] { "blogID", "postID", "iCommentID", "author",
                        "comment", "commentDate", "commentDateFormatted",
                        "status", "url", "email", "postTitle" }, "blogID="
                        + blogID, null, null, null, null);

        int numRows = c.getCount();
        c.moveToFirst();

        for (int i = 0; i < numRows; i++) {
            if (c.getString(0) != null) {
                Map<String, Object> returnHash = new HashMap<String, Object>();
                returnHash.put("blogID", c.getString(0));
                returnHash.put("postID", c.getInt(1));
                returnHash.put("commentID", c.getInt(2));
                returnHash.put("author", c.getString(3));
                returnHash.put("comment", c.getString(4));
                returnHash.put("commentDate", c.getString(5));
                returnHash.put("commentDateFormatted", c.getString(6));
                returnHash.put("status", c.getString(7));
                returnHash.put("url", c.getString(8));
                returnHash.put("email", c.getString(9));
                returnHash.put("postTitle", c.getString(10));
                returnVector.add(i, returnHash);
            }
            c.moveToNext();
        }
        c.close();

        if (numRows == 0) {
            returnVector = null;
        }

        return returnVector;
    }

    public boolean saveComments(List<?> commentValues) {
        boolean returnValue = false;

        Map<?, ?> firstHash = (Map<?, ?>) commentValues.get(0);
        String blogID = firstHash.get("blogID").toString();
        // delete existing values, if user hit refresh button

        try {
            db.delete(COMMENTS_TABLE, "blogID=" + blogID, null);
        } catch (Exception e) {

            return false;
        }

        for (int i = 0; i < commentValues.size(); i++) {
            try {
                ContentValues values = new ContentValues();
                Map<?, ?> thisHash = (Map<?, ?>) commentValues.get(i);
                values.put("blogID", thisHash.get("blogID").toString());
                values.put("postID", thisHash.get("postID").toString());
                values.put("iCommentID", thisHash.get("commentID").toString());
                values.put("author", thisHash.get("author").toString());
                values.put("comment", thisHash.get("comment").toString());
                values.put("commentDate", thisHash.get("commentDate").toString());
                values.put("commentDateFormatted",
                        thisHash.get("commentDateFormatted").toString());
                values.put("status", thisHash.get("status").toString());
                values.put("url", thisHash.get("url").toString());
                values.put("email", thisHash.get("email").toString());
                values.put("postTitle", thisHash.get("postTitle").toString());
                synchronized (this) {
                    try {
                        returnValue = db.insert(COMMENTS_TABLE, null, values) > 0;
                    } catch (Exception e) {

                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return (returnValue);

    }

    public void updateComment(int blogID, int id, Map<?, ?> commentHash) {

        ContentValues values = new ContentValues();
        values.put("author", commentHash.get("author").toString());
        values.put("comment", commentHash.get("comment").toString());
        values.put("status", commentHash.get("status").toString());
        values.put("url", commentHash.get("url").toString());
        values.put("email", commentHash.get("email").toString());

        synchronized (this) {
            db.update(COMMENTS_TABLE, values, "blogID=" + blogID
                    + " AND iCommentID=" + id, null);
        }

    }

    public void updateCommentStatus(int blogID, int id, String newStatus) {

        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        synchronized (this) {
            db.update(COMMENTS_TABLE, values, "blogID=" + blogID
                    + " AND iCommentID=" + id, null);
        }

    }

    public void clearPosts(String blogID) {

        // delete existing values
        db.delete(POSTS_TABLE, "blogID=" + blogID, null);

    }

    // eula table

    public void setStatsDate() {

        ContentValues values = new ContentValues();
        values.put("statsdate", System.currentTimeMillis()); // set to current
                                                                // time
        synchronized (this) {
            db.update(EULA_TABLE, values, "id=0", null);
        }

    }

    public long getStatsDate() {

        Cursor c = db.query(EULA_TABLE, new String[] { "statsdate" }, "id=0",
                null, null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();
        long returnValue = 0;
        if (numRows == 1) {
            returnValue = c.getLong(0);
        }
        c.close();

        return returnValue;
    }

    // categories
    public boolean insertCategory(int id, int wp_id, String category_name) {

        ContentValues values = new ContentValues();
        values.put("blog_id", id);
        values.put("wp_id", wp_id);
        values.put("category_name", category_name.toString());
        boolean returnValue = false;
        synchronized (this) {
            returnValue = db.insert(CATEGORIES_TABLE, null, values) > 0;
        }

        return (returnValue);
    }

    public List<String> loadCategories(int id) {

        Cursor c = db.query(CATEGORIES_TABLE, new String[] { "id", "wp_id",
                "category_name" }, "blog_id=" + id, null, null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();
        List<String> returnVector = new Vector<String>();
        for (int i = 0; i < numRows; ++i) {
            String category_name = c.getString(2);
            if (category_name != null) {
                returnVector.add(category_name);
            }
            c.moveToNext();
        }
        c.close();

        return returnVector;
    }

    public int getCategoryId(int id, String category) {

        Cursor c = db.query(CATEGORIES_TABLE, new String[] { "wp_id" },
                "category_name=\"" + category + "\" AND blog_id=" + id, null,
                null, null, null);
        if (c.getCount() == 0)
            return 0;
        c.moveToFirst();
        int categoryID = 0;
        categoryID = c.getInt(0);

        return categoryID;
    }

    public void clearCategories(int id) {

        // clear out the table since we are refreshing the whole enchilada
        db.delete(CATEGORIES_TABLE, "blog_id=" + id, null);

    }

    // unique identifier queries
    public void updateUUID(String uuid) {

        ContentValues values = new ContentValues();
        values.put("uuid", uuid);
        synchronized (this) {
            db.update("eula", values, null, null);
        }

    }

    public String getUUID() {

        Cursor c = db.query("eula", new String[] { "uuid" }, "id=0", null,
                null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();
        String returnValue = "";
        if (numRows == 1) {
            if (c.getString(0) != null) {
                returnValue = c.getString(0);
            }
        }
        c.close();

        return returnValue;

    }

    public boolean addQuickPressShortcut(int accountId, String name) {

        ContentValues values = new ContentValues();
        values.put("accountId", accountId);
        values.put("name", name);
        boolean returnValue = false;
        synchronized (this) {
            returnValue = db.insert(QUICKPRESS_SHORTCUTS_TABLE, null, values) > 0;
        }

        return (returnValue);
    }

    public List<Map<String, Object>> getQuickPressShortcuts(int accountId) {

        Cursor c = db.query(QUICKPRESS_SHORTCUTS_TABLE, new String[] { "id",
                "accountId", "name" }, "accountId = " + accountId, null, null,
                null, null);
        String id, name;
        int numRows = c.getCount();
        c.moveToFirst();
        List<Map<String, Object>> accounts = new Vector<Map<String, Object>>();
        for (int i = 0; i < numRows; i++) {

            id = c.getString(0);
            name = c.getString(2);
            if (id != null) {
                Map<String, Object> thisHash = new HashMap<String, Object>();

                thisHash.put("id", id);
                thisHash.put("name", name);
                accounts.add(thisHash);
            }
            c.moveToNext();
        }
        c.close();

        return accounts;
    }

    public boolean deleteQuickPressShortcut(String id) {

        int rowsAffected = db.delete(QUICKPRESS_SHORTCUTS_TABLE, "id=" + id,
                null);

        boolean returnValue = false;
        if (rowsAffected > 0) {
            returnValue = true;
        }

        return (returnValue);
    }

    public static String encryptPassword(String clearText) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    PASSWORD_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String encrypedPwd = Base64.encodeToString(cipher.doFinal(clearText
                    .getBytes("UTF-8")), Base64.DEFAULT);
            return encrypedPwd;
        } catch (Exception e) {
        }
        return clearText;
    }

    protected String decryptPassword(String encryptedPwd) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    PASSWORD_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
        }
        return encryptedPwd;
    }

    private void migratePasswords() {

        Cursor c = db.query(SETTINGS_TABLE, new String[] { "id", "password",
                "httppassword", "dotcom_password" }, null, null, null, null,
                null);
        int numRows = c.getCount();
        c.moveToFirst();

        for (int i = 0; i < numRows; i++) {
            ContentValues values = new ContentValues();

            if (c.getString(1) != null) {
                values.put("password", encryptPassword(c.getString(1)));
            }
            if (c.getString(2) != null) {
                values.put("httppassword", encryptPassword(c.getString(2)));
            }
            if (c.getString(3) != null) {
                values.put("dotcom_password", encryptPassword(c.getString(3)));
            }

            db.update(SETTINGS_TABLE, values, "id=" + c.getInt(0), null);

            c.moveToNext();
        }
        c.close();
    }

    public int getUnmoderatedCommentCount(int blogID) {
        int commentCount = 0;

        Cursor c = db
                .rawQuery(
                        "select count(*) from comments where blogID=? AND status='hold'",
                        new String[] { String.valueOf(blogID) });
        int numRows = c.getCount();
        c.moveToFirst();

        if (numRows > 0) {
            commentCount = c.getInt(0);
        }

        c.close();

        return commentCount;
    }

    public boolean saveMediaFile(MediaFile mf) {
        boolean returnValue = false;

        ContentValues values = new ContentValues();
        values.put("postID", mf.getPostID());
        values.put("filePath", mf.getFileName());
        values.put("fileName", mf.getFileName());
        values.put("title", mf.getTitle());
        values.put("description", mf.getDescription());
        values.put("caption", mf.getCaption());
        values.put("horizontalAlignment", mf.getHorizontalAlignment());
        values.put("width", mf.getWidth());
        values.put("height", mf.getHeight());
        values.put("mimeType", mf.getMIMEType());
        values.put("featured", mf.isFeatured());
        values.put("isVideo", mf.isVideo());
        values.put("isFeaturedInPost", mf.isFeaturedInPost());
        synchronized (this) {
            int result = db.update(
                    MEDIA_TABLE,
                    values,
                    "postID=" + mf.getPostID() + " AND filePath='"
                            + mf.getFileName() + "'", null);
            if (result == 0)
                returnValue = db.insert(MEDIA_TABLE, null, values) > 0;
        }

        return (returnValue);
    }

    public MediaFile[] getMediaFilesForPost(Post p) {

        Cursor c = db.query(MEDIA_TABLE, null, "postID=" + p.getId(), null,
                null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();
        MediaFile[] mediaFiles = new MediaFile[numRows];
        for (int i = 0; i < numRows; i++) {

            MediaFile mf = new MediaFile();
            mf.setPostID(c.getInt(1));
            mf.setFilePath(c.getString(2));
            mf.setFileName(c.getString(3));
            mf.setTitle(c.getString(4));
            mf.setDescription(c.getString(5));
            mf.setCaption(c.getString(6));
            mf.setHorizontalAlignment(c.getInt(7));
            mf.setWidth(c.getInt(8));
            mf.setHeight(c.getInt(9));
            mf.setMIMEType(c.getString(10));
            mf.setFeatured(c.getInt(11) > 0);
            mf.setVideo(c.getInt(12) > 0);
            mf.setFeaturedInPost(c.getInt(13) > 0);
            mediaFiles[i] = mf;
            c.moveToNext();
        }
        c.close();

        return mediaFiles;
    }

    public boolean deleteMediaFile(MediaFile mf) {

        boolean returnValue = false;

        int result = 0;
        result = db.delete(MEDIA_TABLE, "id=" + mf.getId(), null);

        if (result == 1) {
            returnValue = true;
        }

        return returnValue;
    }

    public MediaFile getMediaFile(String src, Post post) {

        Cursor c = db.query(MEDIA_TABLE, null, "postID=" + post.getId()
                + " AND filePath='" + src + "'", null, null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();
        MediaFile mf = new MediaFile();
        if (numRows == 1) {
            mf.setPostID(c.getInt(1));
            mf.setFilePath(c.getString(2));
            mf.setFileName(c.getString(3));
            mf.setTitle(c.getString(4));
            mf.setDescription(c.getString(5));
            mf.setCaption(c.getString(6));
            mf.setHorizontalAlignment(c.getInt(7));
            mf.setWidth(c.getInt(8));
            mf.setHeight(c.getInt(9));
            mf.setMIMEType(c.getString(10));
            mf.setFeatured(c.getInt(11) > 0);
            mf.setVideo(c.getInt(12) > 0);
            mf.setFeaturedInPost(c.getInt(13) > 0);
        } else {
            c.close();
            return null;
        }
        c.close();

        return mf;
    }

    public void deleteMediaFilesForPost(Post post) {

        db.delete(MEDIA_TABLE, "postID=" + post.getId(), null);

    }

    public int getWPCOMBlogID() {
        int id = -1;
        Cursor c = db.query(SETTINGS_TABLE, new String[] { "id" },
                "dotcomFlag=1", null, null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();
        if (numRows > 0) {
            id = c.getInt(0);
        }

        c.close();

        return id;
    }

    public void clearComments(int blogID) {

        db.delete(COMMENTS_TABLE, "blogID=" + blogID, null);

    }

    public boolean findLocalChanges() {
        Cursor c = db.query(POSTS_TABLE, null,
                "isLocalChange=1", null, null, null, null);
        int numRows = c.getCount();
        if (numRows > 0) {
            return true;
        }
        c.close();

        return false;
    }

}
