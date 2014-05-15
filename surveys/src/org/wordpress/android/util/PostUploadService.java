package org.wordpress.android.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import org.surveys.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.models.MediaFile;
import org.wordpress.android.models.Post;
import org.wordpress.android.ui.posts.PagesActivity;
import org.wordpress.android.ui.posts.PostsActivity;

public class PostUploadService extends Service {

    public Post post;
    public static Context context;

    private static NotificationManager nm;
    private static int notificationID;
    private static Notification n;

    private static int featuredImageID = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        post = WordPress.currentPost;
        context = this.getApplicationContext();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (post == null || context == null) {
            this.stopSelf();
            return;
        } else {
            new uploadPostTask().execute(post);
        }
    }

    public class uploadPostTask extends AsyncTask<Post, Boolean, Boolean> {

        private Post post;
        String error = "";
        boolean mediaError = false;

        @Override
        protected void onPostExecute(Boolean postUploadedSuccessfully) {

            if (postUploadedSuccessfully) {
                WordPress.postUploaded();
                nm.cancel(notificationID);
            } else {
                String postOrPage = (String) (post.isPage() ? context.getResources().getText(R.string.page_id) : context.getResources()
                        .getText(R.string.post_id));
                Intent notificationIntent = new Intent(context, (post.isPage()) ? PagesActivity.class : PostsActivity.class);
                notificationIntent.setData((Uri.parse("custom://wordpressNotificationIntent" + post.getBlogID())));
                notificationIntent.putExtra("fromNotification", true);
                notificationIntent.putExtra("errorMessage", error);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                n.flags |= Notification.FLAG_AUTO_CANCEL;
                String errorText = context.getResources().getText(R.string.upload_failed).toString();
                if (mediaError)
                    errorText = context.getResources().getText(R.string.media) + " " + context.getResources().getText(R.string.error);
                n.setLatestEventInfo(context, (mediaError) ? errorText : context.getResources().getText(R.string.upload_failed),
                        (mediaError) ? error : postOrPage + " " + errorText + ": " + error, pendingIntent);

                nm.notify(notificationID, n); // needs a unique id
            }

            stopSelf();
        }

        
        @Override
        protected Boolean doInBackground(Post... posts) {

            post = posts[0];

            // add the uploader to the notification bar
            nm = (NotificationManager) context.getSystemService("notification");

            String postOrPage = (String) (post.isPage() ? context.getResources().getText(R.string.page_id) : context.getResources()
                    .getText(R.string.post_id));
            String message = context.getResources().getText(R.string.uploading) + " " + postOrPage;
            n = new Notification(R.drawable.notification_icon, message, System.currentTimeMillis());

            Intent notificationIntent = new Intent(context, PostsActivity.class);
            notificationIntent.setData((Uri.parse("custom://wordpressNotificationIntent" + post.getBlogID())));
            notificationIntent.putExtra("fromNotification", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);

            n.setLatestEventInfo(context, message, message, pendingIntent);

            notificationID = 22 + Integer.valueOf(post.getBlogID());
            nm.notify(notificationID, n); // needs a unique id

            if (post.getPost_status() == null) {
                post.setPost_status("publish");
            }
            Boolean publishThis = false;

            Spannable s;
            String descriptionContent = "", moreContent = "";
            int moreCount = 1;
            if (post.getMt_text_more() != null)
                moreCount++;
            String imgTags = "<img[^>]+android-uri\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
            Pattern pattern = Pattern.compile(imgTags);

            for (int x = 0; x < moreCount; x++) {
                if (post.isLocalDraft()) {
                    if (x == 0)
                        s = (Spannable) WPHtml.fromHtml(post.getDescription(), context, post);
                    else
                        s = (Spannable) WPHtml.fromHtml(post.getMt_text_more(), context, post);
                    WPImageSpan[] click_spans = s.getSpans(0, s.length(), WPImageSpan.class);

                    if (click_spans.length != 0) {

                        for (int i = 0; i < click_spans.length; i++) {
                            String prompt = context.getResources().getText(R.string.uploading_media_item) + String.valueOf(i + 1);
                            n.setLatestEventInfo(context, context.getResources().getText(R.string.uploading) + " " + postOrPage, prompt,
                                    n.contentIntent);
                            nm.notify(notificationID, n);
                            WPImageSpan wpIS = click_spans[i];
                            int start = s.getSpanStart(wpIS);
                            int end = s.getSpanEnd(wpIS);
                            MediaFile mf = new MediaFile();
                            mf.setPostID(post.getId());
                            mf.setTitle(wpIS.getTitle());
                            mf.setCaption(wpIS.getCaption());
                            mf.setDescription(wpIS.getDescription());
                            mf.setFeatured(wpIS.isFeatured());
                            mf.setFeaturedInPost(wpIS.isFeaturedInPost());
                            mf.setFileName(wpIS.getImageSource().toString());
                            mf.setHorizontalAlignment(wpIS.getHorizontalAlignment());
                            mf.setWidth(wpIS.getWidth());

                            String imgHTML = uploadMediaFile(mf);
                            if (imgHTML != null) {
                                SpannableString ss = new SpannableString(imgHTML);
                                s.setSpan(ss, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                s.removeSpan(wpIS);
                            } else {
                                s.removeSpan(wpIS);
                                mediaError = true;
                            }
                        }
                    }

                    if (x == 0)
                        descriptionContent = WPHtml.toHtml(s);
                    else
                        moreContent = WPHtml.toHtml(s);
                } else {
                    Matcher matcher;
                    if (x == 0) {
                        descriptionContent = post.getDescription();
                        matcher = pattern.matcher(descriptionContent);
                    } else {
                        moreContent = post.getMt_text_more();
                        matcher = pattern.matcher(moreContent);
                    }

                    List<String> imageTags = new ArrayList<String>();
                    while (matcher.find()) {
                        imageTags.add(matcher.group());
                    }

                    for (String tag : imageTags) {

                        Pattern p = Pattern.compile("android-uri=\"([^\"]+)\"");
                        Matcher m = p.matcher(tag);
                        String imgPath = "";
                        if (m.find()) {
                            imgPath = m.group(1);
                            if (!imgPath.equals("")) {
                                MediaFile mf = WordPress.wpDB.getMediaFile(imgPath, post);

                                if (mf != null) {
                                    String imgHTML = uploadMediaFile(mf);
                                    if (imgHTML != null) {
                                        if (x == 0) {
                                            descriptionContent = descriptionContent.replace(tag, imgHTML);
                                        } else {
                                            moreContent = moreContent.replace(tag, imgHTML);
                                        }
                                    } else {
                                        if (x == 0)
                                            descriptionContent = descriptionContent.replace(tag, "");
                                        else
                                            moreContent = moreContent.replace(tag, "");
                                        mediaError = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // If media file upload failed, let's stop here and prompt the user
            if (mediaError)
                return false;

            JSONArray categories = post.getCategories();
            String[] theCategories = null;
            if (categories != null) {
                theCategories = new String[categories.length()];
                for (int i = 0; i < categories.length(); i++) {
                    try {
                        theCategories[i] = categories.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            Map<String, Object> contentStruct = new HashMap<String, Object>();

            if (!post.isPage() && post.isLocalDraft()) {
                // add the tagline
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String tagline = "";

                    if (prefs.getBoolean("wp_pref_signature_enabled", false)) {
                        tagline = prefs.getString("wp_pref_post_signature", "");
                        if (tagline != null) {
                            String tag = "\n\n<span class=\"post_sig\">" + tagline + "</span>\n\n";
                            if (moreContent == "")
                                descriptionContent += tag;
                            else
                                moreContent += tag;
                        }
                    }

                // post format
                if (!post.getWP_post_format().equals("")) {
                    if (!post.getWP_post_format().equals("standard"))
                        contentStruct.put("wp_post_format", post.getWP_post_format());
                }
            }

            contentStruct.put("post_type", (post.isPage()) ? "page" : "post");
            contentStruct.put("title", post.getTitle());
            long pubDate = post.getDate_created_gmt();
            if (pubDate != 0) {
                Date date_created_gmt = new Date(pubDate);
                contentStruct.put("date_created_gmt", date_created_gmt);
                Date dateCreated = new Date(pubDate + (date_created_gmt.getTimezoneOffset() * 60000));
                contentStruct.put("dateCreated", dateCreated);
            }

            if (!moreContent.equals("")) {
                descriptionContent = descriptionContent.trim() + "<!--more-->" + moreContent;
                post.setMt_text_more("");
            }

            // get rid of the p and br tags that the editor adds.
            if (post.isLocalDraft()) {
                descriptionContent = descriptionContent.replace("<p>", "").replace("</p>", "\n").replace("<br>", "");
            }

            // gets rid of the weird character android inserts after images
            descriptionContent = descriptionContent.replaceAll("\uFFFC", "");

            contentStruct.put("description", descriptionContent);
            if (!post.isPage()) {
                if (post.getMt_keywords() != "") {
                    contentStruct.put("mt_keywords", post.getMt_keywords());
                }
                if (theCategories != null) {
                    if (theCategories.length > 0)
                        contentStruct.put("categories", theCategories);
                }
            }

            if (post.getMt_excerpt() != null)
                contentStruct.put("mt_excerpt", post.getMt_excerpt());

            contentStruct.put((post.isPage()) ? "page_status" : "post_status", post.getPost_status());
//            Double latitude = 0.0;
//            Double longitude = 0.0;
//            if (!post.isPage()) {
//                latitude = (Double) post.getLatitude();
//                longitude = (Double) post.getLongitude();
//
//                if (latitude > 0) {
//                    Map<Object, Object> hLatitude = new HashMap<Object, Object>();
//                    hLatitude.put("key", "geo_latitude");
//                    hLatitude.put("value", latitude);
//
//                    Map<Object, Object> hLongitude = new HashMap<Object, Object>();
//                    hLongitude.put("key", "geo_longitude");
//                    hLongitude.put("value", longitude);
//
//                    Map<Object, Object> hPublic = new HashMap<Object, Object>();
//                    hPublic.put("key", "geo_public");
//                    hPublic.put("value", 1);
//
//                    Map<Object, Object> testing = new HashMap<Object, Object>();
//                    testing.put("key", "siteCondition");
//                    testing.put("value", post.getSiteCondition());
//
//                    Object[] geo = { hLatitude, hLongitude, hPublic, testing };
//
//
//                    contentStruct.put("custom_fields", geo);
//                }
//            }
            
            if (!post.isPage()){
                Map<Object, Object> asser_name = new HashMap<Object, Object>();
                asser_name.put("key", "rbca_asser_name");
                asser_name.put("value", post.getRBCA_asser_name());
                
                Map<Object, Object> asser_org = new HashMap<Object, Object>();
                asser_org.put("key", "rbca_asser_org");
                asser_org.put("value", post.getRBCA_asser_org());
                
                Map<Object, Object> asser_email = new HashMap<Object, Object>();
                asser_email.put("key", "rbca_asser_email");
                asser_email.put("value", post.getRBCA_asser_email());
                
                Map<Object, Object> asser_phone = new HashMap<Object, Object>();
                asser_phone.put("key", "rbca_asser_phone");
                asser_phone.put("value", post.getRBCA_asser_phone());
                
                Map<Object, Object> coord_latitude = new HashMap<Object, Object>();
                coord_latitude.put("key", "rbca_coord_latitude");
                coord_latitude.put("value", post.getRBCA_coord_latitude());
                
                Map<Object, Object> coord_longitude = new HashMap<Object, Object>();
                coord_longitude.put("key", "rbca_coord_longitude");
                coord_longitude.put("value", post.getRBCA_coord_longitude());
                
                Map<Object, Object> coord_altitude = new HashMap<Object, Object>();
                coord_altitude.put("key", "rbca_coord_altitude");
                coord_altitude.put("value", post.getRBCA_coord_altitude());
                
                Map<Object, Object> coord_accuracy = new HashMap<Object, Object>();
                coord_accuracy.put("key", "rbca_coord_accuracy");
                coord_accuracy.put("value", post.getRBCA_coord_accuracy());
                
                Map<Object, Object> coord_loc = new HashMap<Object, Object>();
                coord_loc.put("key", "rbca_coord_loc");
                coord_loc.put("value", post.getRBCA_coord_loc().toString());
                
                Map<Object, Object> coord_loc_oth = new HashMap<Object, Object>();
                coord_loc_oth.put("key", "rbca_coord_loc_oth");
                coord_loc_oth.put("value", post.getRBCA_coord_loc_oth());
                
                Map<Object, Object> coord_corner = new HashMap<Object, Object>();
                coord_corner.put("key", "rbca_coord_corner");
                coord_corner.put("value", post.getRBCA_coord_corner());
                
                
                Map<Object, Object> coord_notes = new HashMap<Object, Object>();
                coord_notes.put("key", "rbca_coord_notes");
                coord_notes.put("value", post.getRBCA_coord_notes());
                
                Map<Object, Object> addr_no = new HashMap<Object, Object>();
                addr_no.put("key", "rbca_addr_no");
                addr_no.put("value", post.getRBCA_addr_no());
                
                Map<Object, Object> addr_street = new HashMap<Object, Object>();
                addr_street.put("key", "rbca_addr_street");
                addr_street.put("value", post.getRBCA_addr_street());
                
                Map<Object, Object> addr_notes = new HashMap<Object, Object>();
                addr_notes.put("key", "rbca_addr_notes");
                addr_notes.put("value", post.getRBCA_addr_notes());
                
                Map<Object, Object> img_right = new HashMap<Object, Object>();
                img_right.put("key", "rbca_img_right");
                img_right.put("value", post.getRBCA_img_right());
                
                Map<Object, Object> img_front = new HashMap<Object, Object>();
                img_front.put("key", "rbca_img_front");
                img_front.put("value", post.getRBCA_img_front());
                
                Map<Object, Object> img_left = new HashMap<Object, Object>();
                img_left.put("key", "rbca_img_left");
                img_left.put("value", post.getRBCA_img_left());
                
                Map<Object, Object> bldg_name = new HashMap<Object, Object>();
                bldg_name.put("key", "rbca_bldg_name");
                bldg_name.put("value", post.getRBCA_bldg_name());
                
                Map<Object, Object> bldg_is_extant = new HashMap<Object, Object>();
                bldg_is_extant.put("key", "rbca_bldg_is_extant");
                bldg_is_extant.put("value", post.getRBCA_bldg_is_extant());
                
                Map<Object, Object> area = new HashMap<Object, Object>();
                area.put("key", "rbca_bldg_area");
                area.put("value", post.getRBCA_bldg_area());
                System.out.println("Building Area"+ post.getRBCA_bldg_area());
                System.out.println(area);
                
                Map<Object, Object> posting = new HashMap<Object, Object>();
                posting.put("key", "rbca_bldg_posting");
                posting.put("value", post.getRBCA_bldg_posting());
                
                Map<Object, Object> posting_oth = new HashMap<Object, Object>();
                posting_oth.put("key", "rbca_bldg_posting_oth");
                posting_oth.put("value", post.getRBCA_bldg_posting_oth());
                
                Map<Object, Object> bldg_posting_img = new HashMap<Object, Object>();
                bldg_posting_img.put("key", "rbca_bldg_posting_img");
                bldg_posting_img.put("value", post.getRBCA_bldg_posting_img());
                
                Map<Object, Object> occupancy = new HashMap<Object, Object>();
                occupancy.put("key", "rbca_bldg_occucy");
                occupancy.put("value", post.getRBCA_bldg_occucy());
                
                Map<Object, Object> occupancy_available = new HashMap<Object, Object>();
                occupancy_available.put("key", "rbca_bldg_occu_avail");
                occupancy_available.put("value", post.getRBCA_bldg_occucy_avail());
                
                Map<Object, Object> stories = new HashMap<Object, Object>();
                stories.put("key", "rbca_bldg_stories");
                stories.put("value", post.getRBCA_bldg_stories());
                
                Map<Object, Object> width = new HashMap<Object, Object>();
                width.put("key", "rbca_bldg_width");
                width.put("value", post.getRBCA_bldg_width());
                
                Map<Object, Object> length = new HashMap<Object, Object>();
                length.put("key", "rbca_bldg_length");
                length.put("value", post.getRBCA_bldg_length());
                
                Map<Object, Object> uses = new HashMap<Object, Object>();
                uses.put("key", "rbca_bldg_use");
                uses.put("value", post.getRBCA_bldg_use());
                
                Map<Object, Object> uses_oth = new HashMap<Object, Object>();
                uses_oth.put("key", "rbca_bldg_use_oth");
                uses_oth.put("value", post.getRBCA_bldg_use_oth());
                
                Map<Object, Object> outbldg = new HashMap<Object, Object>();
                outbldg.put("key", "rbca_bldg_outbldg");
                outbldg.put("value", post.getRBCA_bldg_outbldg());
                
                Map<Object, Object> outbldg_notes = new HashMap<Object, Object>();
                outbldg_notes.put("key", "rbca_bldg_outbldg_notes");
                outbldg_notes.put("value", post.getRBCA_bldg_outbldg_notes());
                
                Map<Object, Object> units_res = new HashMap<Object, Object>();
                units_res.put("key", "rbca_bldg_units_res");
                units_res.put("value", post.getRBCA_bldg_units_res());
                
                Map<Object, Object> units_comm = new HashMap<Object, Object>();
                units_comm.put("key", "rbca_bldg_units_comm");
                units_comm.put("value", post.getRBCA_bldg_units_comm());
                
                Map<Object, Object> occu_name = new HashMap<Object, Object>();
                occu_name.put("key", "rbca_bldg_occu_name");
                occu_name.put("value", post.getRBCA_bldg_occu_name());
                
                Map<Object, Object> occu_phone = new HashMap<Object, Object>();
                occu_phone.put("key", "rbca_bldg_occu_phone");
                occu_phone.put("value", post.getRBCA_bldg_occu_phone());
                
                Map<Object, Object> bldg_notes = new HashMap<Object, Object>();
                bldg_notes.put("key", "rbca_bldg_notes");
                bldg_notes.put("value", post.getRBCA_bldg_notes());
                
                Map<Object, Object> hist_desig = new HashMap<Object, Object>();
                hist_desig.put("key", "rbca_hist_desig");
                hist_desig.put("value", post.getRBCA_hist_desig());
                
                Map<Object, Object> hist_desig_oth = new HashMap<Object, Object>();
                hist_desig_oth.put("key", "rbca_hist_desig_oth");
                hist_desig_oth.put("value", post.getRBCA_hist_desig_oth());
                
                Map<Object, Object> hist_dist = new HashMap<Object, Object>();
                hist_dist.put("key", "rbca_hist_dist");
                hist_dist.put("value", post.getRBCA_hist_dist());
                
                Map<Object, Object> hist_dist_name = new HashMap<Object, Object>();
                hist_dist_name.put("key", "rbca_hist_dist_name");
                hist_dist_name.put("value", post.getRBCA_hist_dist_name());
                
                Map<Object, Object> hist_appear = new HashMap<Object, Object>();
                hist_appear.put("key", "rbca_hist_appear");
                hist_appear.put("value", post.getRBCA_hist_appear());
                
                Map<Object, Object> hist_age = new HashMap<Object, Object>();
                hist_age.put("key", "rbca_hist_age");
                hist_age.put("value", post.getRBCA_hist_age());
                
                Map<Object, Object> hist_age_meta = new HashMap<Object, Object>();
                hist_age_meta.put("key", "rbca_hist_age_meta");
                hist_age_meta.put("value", post.getRBCA_hist_age_meta());
                
                Map<Object, Object> hist_yr_built = new HashMap<Object, Object>();
                hist_yr_built.put("key", "rbca_hist_yr_built");
                hist_yr_built.put("value", post.getRBCA_hist_yr_built());
                
                Map<Object, Object> hist_age_src = new HashMap<Object, Object>();
                hist_age_src.put("key", "rbca_hist_age_src");
                hist_age_src.put("value", post.getRBCA_hist_age_src());
                
                Map<Object, Object> hist_age_src_oth = new HashMap<Object, Object>();
                hist_age_src_oth.put("key", "rbca_hist_age_src_oth");
                hist_age_src_oth.put("value", post.getRBCA_hist_age_src_oth());
                
                Map<Object, Object> hist_notes = new HashMap<Object, Object>();
                hist_notes.put("key", "rbca_hist_notes");
                hist_notes.put("value", post.getRBCA_hist_notes());
                
                Map<Object, Object> dmg_source = new HashMap<Object, Object>();
                dmg_source.put("key", "rbca_dmg_source");
                dmg_source.put("value", post.getRBCA_dmg_source());
                
                Map<Object, Object> dmg_source_oth = new HashMap<Object, Object>();
                dmg_source_oth.put("key", "rbca_dmg_source_oth");
                dmg_source_oth.put("value", post.getRBCA_dmg_source_oth());
                
                Map<Object, Object> dmg_total = new HashMap<Object, Object>();
                dmg_total.put("key", "rbca_dmg_total");
                dmg_total.put("value", post.getRBCA_dmg_total());
                
                Map<Object, Object> dmg_desc = new HashMap<Object, Object>();
                dmg_desc.put("key", "rbca_dmg_desc");
                dmg_desc.put("value", post.getRBCA_dmg_desc());
                   
                Map<Object, Object> struct_type = new HashMap<Object, Object>();
                struct_type.put("key", "rbca_struct_type");
                struct_type.put("value", post.getRBCA_struct_type());
                
                Map<Object, Object> struct_type_oth = new HashMap<Object, Object>();
                struct_type_oth.put("key", "rbca_struct_type_oth");
                struct_type_oth.put("value", post.getRBCA_struct_type_oth());
                
                Map<Object, Object> struct_defects = new HashMap<Object, Object>();
                struct_defects.put("key", "rbca_struct_defects");
                struct_defects.put("value", post.getRBCA_struct_defects());
                
                Map<Object, Object> struct = new HashMap<Object, Object>();
                struct.put("key", "rbca_struct");
                struct.put("value", post.getRBCA_struct());
                
                Map<Object, Object> struct_notes = new HashMap<Object, Object>();
                struct_notes.put("key", "rbca_struct_notes");
                struct_notes.put("value", post.getRBCA_struct_notes());
                
                Map<Object, Object> found_type = new HashMap<Object, Object>();
                found_type.put("key", "rbca_found_type");
                found_type.put("value", post.getRBCA_found_type());
                
                Map<Object, Object> found_type_oth = new HashMap<Object, Object>();
                found_type_oth.put("key", "rbca_found_type_oth");
                found_type_oth.put("value", post.getRBCA_found_type_oth());
                
                Map<Object, Object> found = new HashMap<Object, Object>();
                found.put("key", "rbca_found");
                found.put("value", post.getRBCA_found());
                
                Map<Object, Object> found_notes = new HashMap<Object, Object>();
                found_notes.put("key", "rbca_found_notes");
                found_notes.put("value", post.getRBCA_found_notes());
                
                Map<Object, Object> extwall_mat = new HashMap<Object, Object>();
                extwall_mat.put("key", "rbca_extwall_mat");
                extwall_mat.put("value", post.getRBCA_extwall_mat());
                
                Map<Object, Object> extwall_mat_oth = new HashMap<Object, Object>();
                extwall_mat_oth.put("key", "rbca_extwall_mat_oth");
                extwall_mat_oth.put("value", post.getRBCA_extwall_mat_oth());
                
                Map<Object, Object> extwall = new HashMap<Object, Object>();
                extwall.put("key", "rbca_extwall");
                extwall.put("value", post.getRBCA_extwall());
                
                Map<Object, Object> extwall_notes = new HashMap<Object, Object>();
                extwall_notes.put("key", "rbca_extwall_notes");
                extwall_notes.put("value", post.getRBCA_extwall_notes());
                
                Map<Object, Object> extfeat_type = new HashMap<Object, Object>();
                extfeat_type.put("key", "rbca_extfeat_type");
                extfeat_type.put("value", post.getRBCA_extfeat_type());
                
                Map<Object, Object> extfeat_type_oth = new HashMap<Object, Object>();
                extfeat_type_oth.put("key", "rbca_extfeat_type_oth");
                extfeat_type_oth.put("value", post.getRBCA_extfeat_type_oth());
                
                Map<Object, Object> extfeat = new HashMap<Object, Object>();
                extfeat.put("key", "rbca_extfeat");
                extfeat.put("value", post.getRBCA_extfeat());
                
                Map<Object, Object> extfeat_notes = new HashMap<Object, Object>();
                extfeat_notes.put("key", "rbca_extfeat_notes");
                extfeat_notes.put("value", post.getRBCA_extfeat_notes());
                
                Map<Object, Object> win_type = new HashMap<Object, Object>();
                win_type.put("key", "rbca_win_type");
                win_type.put("value", post.getRBCA_win_type());
                
                Map<Object, Object> win_type_oth = new HashMap<Object, Object>();
                win_type_oth.put("key", "rbca_win_type_oth");
                win_type_oth.put("value", post.getRBCA_win_type_oth());
                
                Map<Object, Object> win_mat = new HashMap<Object, Object>();
                win_mat.put("key", "rbca_win_mat");
                win_mat.put("value", post.getRBCA_win_mat());
                
                Map<Object, Object> win_mat_oth = new HashMap<Object, Object>();
                win_mat_oth.put("key", "rbca_win_mat_oth");
                win_mat_oth.put("value", post.getRBCA_win_mat_oth());
                
                Map<Object, Object> win = new HashMap<Object, Object>();
                win.put("key", "rbca_win");
                win.put("value", post.getRBCA_win());
                
                Map<Object, Object> win_notes = new HashMap<Object, Object>();
                win_notes.put("key", "rbca_win_notes");
                win_notes.put("value", post.getRBCA_win_notes());
                
                Map<Object, Object> roof_type = new HashMap<Object, Object>();
                roof_type.put("key", "rbca_roof_type");
                roof_type.put("value", post.getRBCA_roof_type());
                
                Map<Object, Object> roof_type_oth = new HashMap<Object, Object>();
                roof_type_oth.put("key", "rbca_roof_type_oth");
                roof_type_oth.put("value", post.getRBCA_roof_type_oth());
                
                Map<Object, Object> roof_mat = new HashMap<Object, Object>();
                roof_mat.put("key", "rbca_roof_mat");
                roof_mat.put("value", post.getRBCA_roof_mat());
                
                Map<Object, Object> roof_mat_oth = new HashMap<Object, Object>();
                roof_mat_oth.put("key", "rbca_roof_mat_oth");
                roof_mat_oth.put("value", post.getRBCA_roof_mat_oth());
                
                Map<Object, Object> roof = new HashMap<Object, Object>();
                roof.put("key", "rbca_roof");
                roof.put("value", post.getRBCA_roof());
                
                Map<Object, Object> roof_notes = new HashMap<Object, Object>();
                roof_notes.put("key", "rbca_roof_notes");
                roof_notes.put("value", post.getRBCA_roof_notes());
                
                Map<Object, Object> int_cond = new HashMap<Object, Object>();
                int_cond.put("key", "rbca_int_cond");
                int_cond.put("value", post.getRBCA_int_cond());
                
                Map<Object, Object> int_collect_extant = new HashMap<Object, Object>();
                int_collect_extant.put("key", "rbca_int_collect_extant");
                int_collect_extant.put("value", post.getRBCA_int_collect_extant());
                
                Map<Object, Object> int_collect_type = new HashMap<Object, Object>();
                int_collect_type.put("key", "rbca_int_collect_type");
                int_collect_type.put("value", post.getRBCA_int_collect_type());
                
                Map<Object, Object> int_collect_type_oth = new HashMap<Object, Object>();
                int_collect_type_oth.put("key", "rbca_int_collect_type_oth");
                int_collect_type_oth.put("value", post.getRBCA_int_collect_type_oth());
                
                Map<Object, Object> int_img1 = new HashMap<Object, Object>();
                int_img1.put("key", "rbca_int_img1");
                int_img1.put("value", post.getRBCA_int_img1());
                
                Map<Object, Object> int_desc1 = new HashMap<Object, Object>();
                int_desc1.put("key", "rbca_int_desc1");
                int_desc1.put("value", post.getRBCA_int_desc1());
                
                Map<Object, Object> int_img2 = new HashMap<Object, Object>();
                int_img2.put("key", "rbca_int_img2");
                int_img2.put("value", post.getRBCA_int_img2());
                
                Map<Object, Object> int_desc2 = new HashMap<Object, Object>();
                int_desc2.put("key", "rbca_int_desc2");
                int_desc2.put("value", post.getRBCA_int_desc2());
                
                Map<Object, Object> int_img3 = new HashMap<Object, Object>();
                int_img3.put("key", "rbca_int_img3");
                int_img3.put("value", post.getRBCA_int_img3());
                
                Map<Object, Object> int_desc3 = new HashMap<Object, Object>();
                int_desc3.put("key", "rbca_int_desc3");
                int_desc3.put("value", post.getRBCA_int_desc3());
                
                Map<Object, Object> int_notes = new HashMap<Object, Object>();
                int_notes.put("key", "rbca_int_notes");
                int_notes.put("value", post.getRBCA_int_notes());
                
                Map<Object, Object> landveg_feat = new HashMap<Object, Object>();
                landveg_feat.put("key", "rbca_landveg_feat");
                landveg_feat.put("value", post.getRBCA_landveg_feat());
                
                Map<Object, Object> landveg_feat_oth = new HashMap<Object, Object>();
                landveg_feat_oth.put("key", "rbca_landveg_feat_oth");
                landveg_feat_oth.put("value", post.getRBCA_landveg_feat_oth());
                
                Map<Object, Object> landveg = new HashMap<Object, Object>();
                landveg.put("key", "rbca_landveg");
                landveg.put("value", post.getRBCA_landveg());
                
                Map<Object, Object> landveg_notes = new HashMap<Object, Object>();
                landveg_notes.put("key", "rbca_landveg_notes");
                landveg_notes.put("value", post.getRBCA_landveg_notes());
                
                Map<Object, Object> landblt_feat = new HashMap<Object, Object>();
                landblt_feat.put("key", "rbca_landblt_feat");
                landblt_feat.put("value", post.getRBCA_landblt_feat());
                
                Map<Object, Object> landblt_feat_oth = new HashMap<Object, Object>();
                landblt_feat_oth.put("key", "rbca_landblt_feat_oth");
                landblt_feat_oth.put("value", post.getRBCA_landblt_feat_oth());
                
                Map<Object, Object> landblt = new HashMap<Object, Object>();
                landblt.put("key", "rbca_landblt");
                landblt.put("value", post.getRBCA_landblt());
                
                Map<Object, Object> landblt_notes = new HashMap<Object, Object>();
                landblt_notes.put("key", "rbca_landblt_notes");
                landblt_notes.put("value", post.getRBCA_landblt_notes());
                
                Map<Object, Object> media_img1 = new HashMap<Object, Object>();
                media_img1.put("key", "rbca_media_img1");
                media_img1.put("value", post.getRBCA_media_img1());
                
                Map<Object, Object> media_desc1 = new HashMap<Object, Object>();
                media_desc1.put("key", "rbca_media_desc1");
                media_desc1.put("value", post.getRBCA_media_desc1());
                
                Map<Object, Object> media_img2 = new HashMap<Object, Object>();
                media_img2.put("key", "rbca_media_img2");
                media_img2.put("value", post.getRBCA_media_img2());
                
                Map<Object, Object> media_desc2 = new HashMap<Object, Object>();
                media_desc2.put("key", "rbca_media_desc2");
                media_desc2.put("value", post.getRBCA_media_desc2());
                
                Map<Object, Object> media_img3 = new HashMap<Object, Object>();
                media_img3.put("key", "rbca_media_img3");
                media_img3.put("value", post.getRBCA_media_img3());
                
                Map<Object, Object> media_desc3 = new HashMap<Object, Object>();
                media_desc3.put("key", "rbca_media_desc3");
                media_desc3.put("value", post.getRBCA_media_desc3());
                
                Map<Object, Object> media_img4 = new HashMap<Object, Object>();
                media_img4.put("key", "rbca_media_img4");
                media_img4.put("value", post.getRBCA_media_img4());
                
                Map<Object, Object> media_desc4 = new HashMap<Object, Object>();
                media_desc4.put("key", "rbca_media_desc4");
                media_desc4.put("value", post.getRBCA_media_desc4());
                
                Map<Object, Object> media_img5 = new HashMap<Object, Object>();
                media_img5.put("key", "rbca_media_img5");
                media_img5.put("value", post.getRBCA_media_img5());
                
                Map<Object, Object> media_desc5 = new HashMap<Object, Object>();
                media_desc5.put("key", "rbca_media_desc5");
                media_desc5.put("value", post.getRBCA_media_desc5());
                
                Map<Object, Object> media_img6 = new HashMap<Object, Object>();
                media_img6.put("key", "rbca_media_img6");
                media_img6.put("value", post.getRBCA_media_img6());
                
                Map<Object, Object> media_desc6 = new HashMap<Object, Object>();
                media_desc6.put("key", "rbca_media_desc6");
                media_desc6.put("value", post.getRBCA_media_desc6());
                
                Map<Object, Object> hzrd = new HashMap<Object, Object>();
                hzrd.put("key", "rbca_hzrd");
                hzrd.put("value", post.getRBCA_hzrd());
                
                Map<Object, Object> hzrd_type = new HashMap<Object, Object>();
                hzrd_type.put("key", "rbca_hzrd_type");
                hzrd_type.put("value", post.getRBCA_hzrd_type());
                
                Map<Object, Object> hzrd_type_oth = new HashMap<Object, Object>();
                hzrd_type_oth.put("key", "rbca_hzrd_type_oth");
                hzrd_type_oth.put("value", post.getRBCA_hzrd_type_oth());
                
                Map<Object, Object> hzrd_notes = new HashMap<Object, Object>();
                hzrd_notes.put("key", "rbca_hzrd_notes");
                hzrd_notes.put("value", post.getRBCA_hzrd_notes());
                
                Map<Object, Object> hzrd_hazmat = new HashMap<Object, Object>();
                hzrd_hazmat.put("key", "rbca_hzrd_hazmat");
                hzrd_hazmat.put("value", post.getRBCA_hzrd_hazmat());
                
                Map<Object, Object> hzrd_hazmat_oth = new HashMap<Object, Object>();
                hzrd_hazmat_oth.put("key", "rbca_hzrd_hazmat_oth");
                hzrd_hazmat_oth.put("value", post.getRBCA_hzrd_hazmat_oth());
                
                Map<Object, Object> actn = new HashMap<Object, Object>();
                actn.put("key", "rbca_actn");
                actn.put("value", post.getRBCA_actn());
                
                Map<Object, Object> actn_oth = new HashMap<Object, Object>();
                actn_oth.put("key", "rbca_actn_oth");
                actn_oth.put("value", post.getRBCA_actn_oth());
                
                Map<Object, Object> eval = new HashMap<Object, Object>();
                eval.put("key", "rbca_eval");
                eval.put("value", post.getRBCA_eval());
                
                Map<Object, Object> eval_oth = new HashMap<Object, Object>();
                eval_oth.put("key", "rbca_eval_oth");
                eval_oth.put("value", post.getRBCA_eval_oth());
    
                Object[] geo = { asser_name, asser_org, asser_email, asser_phone, coord_latitude, coord_longitude, coord_altitude,
                                coord_accuracy, coord_loc, coord_loc_oth, coord_corner, coord_notes,
                                addr_no, addr_street, addr_notes, img_right, img_front, img_left, bldg_name, 
                                bldg_is_extant, posting, posting_oth , bldg_posting_img, occupancy,
                                occupancy_available, stories,width,length,uses,uses_oth,outbldg,
                                outbldg_notes, units_res, units_comm, occu_name, occu_phone, 
                                bldg_notes, hist_desig, hist_desig_oth, hist_dist, hist_dist_name,hist_appear,hist_age,
                                hist_age_meta,hist_yr_built,hist_age_src,hist_age_src_oth, hist_notes,dmg_source,dmg_source_oth,dmg_total,dmg_desc,struct_type,
                                struct_type_oth,struct_defects, struct,struct_notes,found_type,found_type_oth,found,found_notes,
                                extwall_mat,extwall_mat_oth,extwall,extwall_notes,extfeat_type,extfeat_type_oth,extfeat,
                                extfeat_notes,win_type,win_type_oth,win_mat,win_mat_oth,win,win_notes,roof_type,roof_type_oth,
                                roof_mat,roof_mat_oth,roof,roof_notes,int_cond,int_collect_extant,int_collect_type,
                                int_collect_type_oth,int_img1, int_desc1, int_img2, int_desc2, int_img3, int_desc3, 
                                int_notes,landveg_feat,landveg_feat_oth,landveg,landveg_notes,
                                landblt_feat,landblt_feat_oth,landblt,landblt_notes,
                                media_img1, media_desc1, media_img2, media_img2, media_img3, media_desc3, media_img4, media_desc4, 
                                media_img5, media_desc5, media_img6, media_desc6,
                                hzrd, hzrd_type, hzrd_type_oth, 
                                hzrd_notes, hzrd_hazmat, hzrd_hazmat_oth,actn, actn_oth, eval, eval_oth};
    
                contentStruct.put("custom_fields", geo);
            }

            // featured image
            if (featuredImageID != -1)
                contentStruct.put("wp_post_thumbnail", featuredImageID);

            XMLRPCClient client = new XMLRPCClient(post.getBlog().getUrl(), post.getBlog().getHttpuser(), post.getBlog().getHttppassword());

            if (post.getQuickPostType() != null)
                client.addQuickPostHeader(post.getQuickPostType());

            n.setLatestEventInfo(context, message, message, n.contentIntent);
            nm.notify(notificationID, n);
            if (post.getWP_password() != null) {
                contentStruct.put("wp_password", post.getWP_password());
            }
            Object[] params;

            if (post.isLocalDraft() && !post.isUploaded())
                params = new Object[] { post.getBlog().getBlogId(), post.getBlog().getUsername(), post.getBlog().getPassword(),
                        contentStruct, publishThis };
            else
                params = new Object[] { post.getPostid(), post.getBlog().getUsername(), post.getBlog().getPassword(), contentStruct,
                        publishThis };

            try {
                client.call((post.isLocalDraft() && !post.isUploaded()) ? "metaWeblog.newPost" : "metaWeblog.editPost", params);
                post.setUploaded(true);
                post.setLocalChange(false);
                post.update();
                return true;
            } catch (final XMLRPCException e) {
                error = String.format(context.getResources().getText(R.string.error_upload).toString(), post.isPage() ? context
                        .getResources().getText(R.string.page).toString() : context.getResources().getText(R.string.post).toString())
                        + " " + cleanXMLRPCErrorMessage(e.getMessage());
                mediaError = false;
                Log.i("WP", error);
            }

            return false;
        }

        public String uploadMediaFile(MediaFile mf) {
            String content = "";

            // image variables
            String finalThumbnailUrl = null;
            String finalImageUrl = null;

            // check for image, and upload it
            if (mf.getFileName() != null) {
                XMLRPCClient client = new XMLRPCClient(post.getBlog().getUrl(), post.getBlog().getHttpuser(), post.getBlog()
                        .getHttppassword());

                String curImagePath = "";

                curImagePath = mf.getFileName();
                boolean video = false;
                if (curImagePath.contains("video")) {
                    video = true;
                }

                if (video) { // upload the video

                    // create temp file for media upload
                    String tempFileName = "wp-" + System.currentTimeMillis();
                    try {
                        context.openFileOutput(tempFileName, Context.MODE_PRIVATE);
                    } catch (FileNotFoundException e) {
                        error = getResources().getString(R.string.file_error_create);
                        mediaError = true;
                        return null;
                    }

                    File tempFile = context.getFileStreamPath(tempFileName);

                    Uri videoUri = Uri.parse(curImagePath);
                    File fVideo = null;
                    String mimeType = "", xRes = "", yRes = "";

                    if (videoUri.toString().contains("content:")) {
                        String[] projection;
                        Uri imgPath;

                        projection = new String[] { Video.Media._ID, Video.Media.DATA, Video.Media.MIME_TYPE, Video.Media.RESOLUTION };
                        imgPath = videoUri;

                        Cursor cur = context.getContentResolver().query(imgPath, projection, null, null, null);
                        String thumbData = "";

                        if (cur.moveToFirst()) {

                            int mimeTypeColumn, resolutionColumn, dataColumn;

                            dataColumn = cur.getColumnIndex(Video.Media.DATA);
                            mimeTypeColumn = cur.getColumnIndex(Video.Media.MIME_TYPE);
                            resolutionColumn = cur.getColumnIndex(Video.Media.RESOLUTION);

                            mf = new MediaFile();

                            thumbData = cur.getString(dataColumn);
                            mimeType = cur.getString(mimeTypeColumn);
                            fVideo = new File(thumbData);
                            mf.setFilePath(fVideo.getPath());
                            String resolution = cur.getString(resolutionColumn);
                            if (resolution != null) {
                                String[] resx = resolution.split("x");
                                xRes = resx[0];
                                yRes = resx[1];
                            } else {
                                // set the width of the video to the
                                // thumbnail
                                // width, else 640x480
                                if (!post.getBlog().getMaxImageWidth().equals("Original Size")) {
                                    xRes = post.getBlog().getMaxImageWidth();
                                    yRes = String.valueOf(Math.round(Integer.valueOf(post.getBlog().getMaxImageWidth()) * 0.75));
                                } else {
                                    xRes = "640";
                                    yRes = "480";
                                }

                            }

                        }
                    } else { // file is not in media library
                        fVideo = new File(videoUri.toString().replace("file://", ""));
                    }

                    String imageTitle = fVideo.getName();

                    // try to upload the video
                    Map<String, Object> m = new HashMap<String, Object>();

                    m.put("name", imageTitle);
                    m.put("type", mimeType);
                    m.put("bits", mf);
                    m.put("overwrite", true);

                    Object[] params = { 1, post.getBlog().getUsername(), post.getBlog().getPassword(), m };

                    Object result = null;

                    try {
                        result = (Object) client.callUploadFile("wp.uploadFile", params, tempFile);
                    } catch (XMLRPCException e) {
                        error = context.getResources().getString(R.string.error_media_upload) + ": " + cleanXMLRPCErrorMessage(e.getMessage());
                        return null;
                    }

                    Map<?, ?> contentHash = (HashMap<?, ?>) result;

                    String resultURL = contentHash.get("url").toString();
                    if (contentHash.containsKey("videopress_shortcode")) {
                        resultURL = contentHash.get("videopress_shortcode").toString() + "\n";
                    } else {
                        resultURL = String
                                .format("<video width=\"%s\" height=\"%s\" controls=\"controls\"><source src=\"%s\" type=\"%s\" /><a href=\"%s\">Click to view video</a>.</video>",
                                        xRes, yRes, resultURL, mimeType, resultURL);
                    }

                    content = content + resultURL;

                } // end video
                else {
                    for (int i = 0; i < 2; i++) {

                        // create temp file for media upload
                        String tempFileName = "wp-" + System.currentTimeMillis();
                        try {
                            context.openFileOutput(tempFileName, Context.MODE_PRIVATE);
                        } catch (FileNotFoundException e) {
                            mediaError = true;
                            error = context.getString(R.string.file_not_found);
                            return null;
                        }

                        File tempFile = context.getFileStreamPath(tempFileName);

                        curImagePath = mf.getFileName();

                        if (i == 0
                                || (((post.getBlog().isFullSizeImage() && !post.getBlog().getMaxImageWidth().equals("Original Size")) || post
                                        .getBlog().isScaledImage()))) {

                            Uri imageUri = Uri.parse(curImagePath);
                            File jpeg = null;
                            String mimeType = "", orientation = "", path = "";

                            if (imageUri.toString().contains("content:")) {
                                String[] projection;
                                Uri imgPath;

                                projection = new String[] { Images.Media._ID, Images.Media.DATA, Images.Media.MIME_TYPE,
                                        Images.Media.ORIENTATION };

                                imgPath = imageUri;

                                Cursor cur = context.getContentResolver().query(imgPath, projection, null, null, null);
                                String thumbData = "";

                                if (cur.moveToFirst()) {

                                    int dataColumn, mimeTypeColumn, orientationColumn;

                                    dataColumn = cur.getColumnIndex(Images.Media.DATA);
                                    mimeTypeColumn = cur.getColumnIndex(Images.Media.MIME_TYPE);
                                    orientationColumn = cur.getColumnIndex(Images.Media.ORIENTATION);

                                    orientation = cur.getString(orientationColumn);
                                    thumbData = cur.getString(dataColumn);
                                    mimeType = cur.getString(mimeTypeColumn);
                                    jpeg = new File(thumbData);
                                    path = thumbData;
                                    mf.setFilePath(jpeg.getPath());
                                }
                            } else { // file is not in media library
                                path = imageUri.toString().replace("file://", "");
                                jpeg = new File(path);
                                String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                                if (extension != null) {
                                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                                    mimeType = mime.getMimeTypeFromExtension(extension);
                                    if (mimeType == null)
                                        mimeType = "image/jpeg";
                                }
                                mf.setFilePath(path);
                            }

                            // check if the file exists
                            if (jpeg == null) {
                                error = context.getString(R.string.file_not_found);
                                mediaError = true;
                                return null;
                            }

                            ImageHelper ih = new ImageHelper();
                            orientation = ih.getExifOrientation(path, orientation);

                            String imageTitle = jpeg.getName();

                            byte[] finalBytes = null;

                            if (i == 0 || post.getBlog().isScaledImage()) {
                                byte[] bytes;
                                try {
                                    bytes = new byte[(int) jpeg.length()];
                                } catch (OutOfMemoryError er) {
                                    error = context.getString(R.string.out_of_memory);
                                    mediaError = true;
                                    return null;
                                }

                                DataInputStream in = null;
                                try {
                                    in = new DataInputStream(new FileInputStream(jpeg));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    in.readFully(bytes);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String width = String.valueOf(i == 0 ? mf.getWidth() : post.getBlog().getScaledImageWidth());
                                if (post.getBlog().getMaxImageWidth().equals("Original Size") && i == 0)
                                    width = "Original Size";

                                ImageHelper ih2 = new ImageHelper();
                                finalBytes = ih2.createThumbnail(bytes, width, orientation, false);

                                if (finalBytes == null) {
                                    error = context.getString(R.string.out_of_memory);
                                    mediaError = true;
                                    return null;
                                }
                            }

                            // try to upload the image
                            Map<String, Object> m = new HashMap<String, Object>();

                            m.put("name", imageTitle);
                            m.put("type", mimeType);
                            if (i == 0 || post.getBlog().isScaledImage()) {
                                m.put("bits", finalBytes);
                            } else {
                                m.put("bits", mf);
                            }
                            m.put("overwrite", true);

                            Object[] params = { 1, post.getBlog().getUsername(), post.getBlog().getPassword(), m };

                            Object result = null;

                            try {
                                result = (Object) client.callUploadFile("wp.uploadFile", params, tempFile);
                            } catch (XMLRPCException e) {
                                error = context.getResources().getString(R.string.error_media_upload) + ": " + cleanXMLRPCErrorMessage(e.getMessage());
                                mediaError = true;
                                return null;
                            }

                            Map<?, ?> contentHash = (HashMap<?, ?>) result;

                            String resultURL = contentHash.get("url").toString();

                            if (mf.isFeatured()) {
                                try {
                                    if (contentHash.get("id") != null) {
                                        featuredImageID = Integer.parseInt(contentHash.get("id").toString());
                                        if (!mf.isFeaturedInPost())
                                            return "";
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (i == 0) {
                                finalThumbnailUrl = resultURL;
                            } else {
                                if (post.getBlog().isFullSizeImage() || post.getBlog().isScaledImage()) {
                                    finalImageUrl = resultURL;
                                } else {
                                    finalImageUrl = "";
                                }
                            }

                            String alignment = "";
                            switch (mf.getHorizontalAlignment()) {
                            case 0:
                                alignment = "alignnone";
                                break;
                            case 1:
                                alignment = "alignleft";
                                break;
                            case 2:
                                alignment = "aligncenter";
                                break;
                            case 3:
                                alignment = "alignright";
                                break;
                            }

                            String alignmentCSS = "class=\"" + alignment + " size-full\" ";
                            if (resultURL != null) {
                                if (i != 0 && (post.getBlog().isFullSizeImage() || post.getBlog().isScaledImage())) {
                                    content = content + "<a href=\"" + finalImageUrl + "\"><img title=\"" + mf.getTitle() + "\" "
                                            + alignmentCSS + "alt=\"image\" src=\"" + finalThumbnailUrl + "\" /></a>";
                                } else {
                                    if (i == 0
                                            && (post.getBlog().isFullSizeImage() == false && !post.getBlog().isScaledImage())
                                            || (post.getBlog().getMaxImageWidth().equals("Original Size") && !post.getBlog()
                                                    .isScaledImage())) {
                                        content = content + "<a href=\"" + finalThumbnailUrl + "\"><img title=\"" + mf.getTitle() + "\" "
                                                + alignmentCSS + "alt=\"image\" src=\"" + finalThumbnailUrl + "\" /></a>";
                                    }
                                }

                                if ((i == 0 && (!post.getBlog().isFullSizeImage() && !post.getBlog().isScaledImage()) || (post.getBlog()
                                        .getMaxImageWidth().equals("Original Size") && !post.getBlog().isScaledImage()))
                                        || i == 1) {
                                    if (!mf.getCaption().equals("")) {
                                        content = String.format("[caption id=\"\" align=\"%s\" width=\"%d\" caption=\"%s\"]%s[/caption]",
                                                alignment, mf.getWidth(), EscapeUtils.escapeHtml(mf.getCaption()), content);
                                    }
                                }
                            }

                        } // end if statement
                    }// end image check
                }
            }// end image stuff
            return content;
        }
    }

    public String cleanXMLRPCErrorMessage(String message) {
        if (message != null) {
            if (message.indexOf(": ") > -1)
                message = message.substring(message.indexOf(": ") + 2, message.length());
            if (message.indexOf("[code") > -1)
                message = message.substring(0, message.indexOf("[code"));
            return message;
        } else {
            return "";
        }
    }
}
