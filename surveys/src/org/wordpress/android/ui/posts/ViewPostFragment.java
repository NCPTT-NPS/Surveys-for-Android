package org.wordpress.android.ui.posts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.surveys.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.models.Post;
import org.wordpress.android.util.EscapeUtils;
import org.wordpress.android.util.StringHelper;

public class ViewPostFragment extends Fragment {
    /** Called when the activity is first created. */

    private OnDetailPostActionListener onDetailPostActionListener;
    PostsActivity parentActivity;

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (WordPress.currentPost != null)
            loadPost(WordPress.currentPost);

        parentActivity = (PostsActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.viewpost, container, false);

        // button listeners here
        ImageButton editPostButton = (ImageButton) v
                .findViewById(R.id.editPost);
        editPostButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                if (WordPress.currentPost != null && !parentActivity.isRefreshing) {
                    onDetailPostActionListener.onDetailPostAction(
                            PostsActivity.POST_EDIT, WordPress.currentPost);
                    Intent i = new Intent(
                            getActivity().getApplicationContext(),
                            EditPostActivity.class);
                    i.putExtra("isPage", WordPress.currentPost.isPage());
                    i.putExtra("postID", WordPress.currentPost.getId());
                    i.putExtra("localDraft", WordPress.currentPost.isLocalDraft());
                    startActivityForResult(i, 0);
                }

            }
        });

        ImageButton shareURLButton = (ImageButton) v
                .findViewById(R.id.sharePostLink);
        shareURLButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {

                if (!parentActivity.isRefreshing)
                    onDetailPostActionListener.onDetailPostAction(PostsActivity.POST_SHARE, WordPress.currentPost);

            }
        });

        ImageButton deletePostButton = (ImageButton) v
                .findViewById(R.id.deletePost);
        deletePostButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {

                if (!parentActivity.isRefreshing)
                    onDetailPostActionListener.onDetailPostAction(PostsActivity.POST_DELETE, WordPress.currentPost);

            }
        });

        ImageButton viewPostButton = (ImageButton) v
                .findViewById(R.id.viewPost);
        viewPostButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {

                if (!parentActivity.isRefreshing)
                    loadPostPreview();

            }
        });
        
        ImageButton addCommentButton = (ImageButton) v
                .findViewById(R.id.addComment);
        addCommentButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {

                if (!parentActivity.isRefreshing)
                    onDetailPostActionListener.onDetailPostAction(PostsActivity.POST_COMMENT, WordPress.currentPost);

            }
        });

        return v;

    }

    protected void loadPostPreview() {

        if (WordPress.currentPost != null) {
            if (WordPress.currentPost.getPermaLink() != null && !WordPress.currentPost.getPermaLink().equals("")) {
                Intent i = new Intent(getActivity(), PreviewPostActivity.class);
                startActivity(i);
            }
        }

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // check that the containing activity implements our callback
            onDetailPostActionListener = (OnDetailPostActionListener) activity;
        } catch (ClassCastException e) {
            activity.finish();
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    public void loadPost(Post post) {

        // Don't load if the Post object of title are null, see #395
        if (post == null || post.getTitle() == null)
            return;

        TextView title = (TextView) getActivity().findViewById(R.id.postTitle);
        if (post.getTitle().equals(""))
            title.setText("(" + getResources().getText(R.string.untitled) + ")");
        else
            title.setText(EscapeUtils.unescapeHtml(post.getTitle()));

        WebView webView = (WebView) getActivity().findViewById(
                R.id.viewPostWebView);
        TextView tv = (TextView) getActivity().findViewById(
                R.id.viewPostTextView);
        ImageButton shareURLButton = (ImageButton) getActivity().findViewById(
                R.id.sharePostLink);
        ImageButton viewPostButton = (ImageButton) getActivity().findViewById(
                R.id.viewPost);
        ImageButton addCommentButton = (ImageButton) getActivity().findViewById(
                R.id.addComment);

        tv.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        String html = StringHelper.addPTags(post.getDescription()
                + "\n\n" + post.getMt_text_more() 
                + "\n\n" + "Coordinate Location: "+post.getRBCA_coord_loc()
                + "\n\n" + "Coordinate Location Other: "+post.getRBCA_coord_loc_oth()
                + "\n\n" + "Coordinate Corner: "+post.getRBCA_coord_corner()
                + "\n\n" + "Coordinate Notes: "+post.getRBCA_coord_notes()
                + "\n\n" + "Address Number: "+post.getRBCA_addr_no()
                + "\n\n" + "Address Street: "+post.getRBCA_addr_street()
                + "\n\n" + "Area Assessed: "+post.getRBCA_bldg_area()
                + "\n\n" + "Posting: "+post.getRBCA_bldg_posting()
                + "\n\n" + "Posting Other: "+post.getRBCA_bldg_posting_oth()
                + "\n\n" + "Occupancy: "+post.getRBCA_bldg_occucy() 
                + "\n\n" + "Occupancy Available: "+post.getRBCA_bldg_occucy_avail()
                + "\n\n" + "# of Stories: "+post.getRBCA_bldg_stories() 
                + "\n\n" + "Width: "+post.getRBCA_bldg_width()
                + "\n\n" + "Length: "+post.getRBCA_bldg_length()
                + "\n\n" + "Use(s): "+post.getRBCA_bldg_use()
                + "\n\n" + "Uses_other: "+post.getRBCA_bldg_use_oth()
                + "\n\n" + "Outbuildings: "+post.getRBCA_bldg_outbldg()
                + "\n\n" + "Outbuildings notes: "+post.getRBCA_bldg_outbldg_notes()
                + "\n\n" + "# Residential Units: "+post.getRBCA_bldg_units_res()
                + "\n\n" + "# Commercial Units: "+post.getRBCA_bldg_units_comm()
                + "\n\n" + "Occupant Name: "+post.getRBCA_bldg_occu_name()
                + "\n\n" + "Occupant Phone: "+post.getRBCA_bldg_occu_phone()
                + "\n\n" + "Notes: "+post.getRBCA_bldg_notes()
                + "\n\n" + "Historic Designation: "+post.getRBCA_hist_desig()
                + "\n\n" + "Hist Designation Other: "+post.getRBCA_hist_desig_oth()
                + "\n\n" + "Located Historic District?: "+post.getRBCA_hist_dist()
                + "\n\n" + "Historic Distrit Name: "+post.getRBCA_hist_dist_name()
                + "\n\n" + "Potentially Eligible: "+post.getRBCA_hist_appear()
                + "\n\n" + "Building Age: "+post.getRBCA_hist_age()
                + "\n\n" + "Age Is: "+post.getRBCA_hist_age_meta()
                + "\n\n" + "Actual Year Built: "+post.getRBCA_hist_yr_built()
                + "\n\n" + "Damage Date: "+post.getRBCA_dmg_date()
                + "\n\n" + "Damage Source: "+post.getRBCA_dmg_source()
                + "\n\n" + "Damage Source Oth: "+post.getRBCA_dmg_source_oth()
                + "\n\n" + "Damage Total: "+post.getRBCA_dmg_total()
                + "\n\n" + "Damage Description: "+post.getRBCA_dmg_desc()
                + "\n\n" + "Nature of Water: "+post.getRBCA_flood_water()
                + "\n\n" + "Specify Other: "+post.getRBCA_flood_water_oth()
                + "\n\n" + "Space Water Entered: "+post.getRBCA_flood_entry()
                + "\n\n" + "Specify Other: "+post.getRBCA_flood_entry_oth()
                + "\n\n" + "Depth of water(feet): "+post.getRBCA_flood_depth()
                + "\n\n" + "Sediment on site: "+post.getRBCA_flood_sed()
                + "\n\n" + "Specify Other: "+post.getRBCA_flood_sed_oth()
                + "\n\n" + "Flood Notes: "+post.getRBCA_flood_notes()
                + "\n\n" + "Type of Structure: "+post.getRBCA_struct_type()
                + "\n\n" + "Specify Other: "+post.getRBCA_struct_type_oth()
                + "\n\n" + "Structure Damage: "+post.getRBCA_struct()
                + "\n\n" + "Structure Notes: "+post.getRBCA_struct_notes()
                + "\n\n" + "Foundation type: "+post.getRBCA_found_type()
                + "\n\n" + "Specify Other: "+post.getRBCA_found_type_oth()
                + "\n\n" + "Foundation Damage: "+post.getRBCA_found()
                + "\n\n" + "Foundation Notes: "+post.getRBCA_found_notes()
                + "\n\n" + "Exterior Wall Materials: "+post.getRBCA_extwall_mat()
                + "\n\n" + "Specify Other: "+post.getRBCA_extwall_mat_oth()
                + "\n\n" + "Exterior Wall Damage: "+post.getRBCA_extwall()
                + "\n\n" + "Exterior Wall Notes: "+post.getRBCA_extwall_notes()
                + "\n\n" + "Exterior Features Type: "+post.getRBCA_extfeat_type()
                + "\n\n" + "Specify Other: "+post.getRBCA_extfeat_type_oth()
                + "\n\n" + "Exterior Features Damage: "+post.getRBCA_extfeat()
                + "\n\n" + "Exterior Features Notes: "+post.getRBCA_extfeat_notes()
                + "\n\n" + "Window Type: "+post.getRBCA_win_type()
                + "\n\n" + "Specify Other: "+post.getRBCA_win_type_oth()
                + "\n\n" + "Window Sash Material: "+post.getRBCA_win_mat()
                + "\n\n" + "Specify other: "+post.getRBCA_win_type_oth()
                + "\n\n" + "Window Damage: "+post.getRBCA_win()
                + "\n\n" + "Window Notes: "+post.getRBCA_win_notes()
                + "\n\n" + "Roof Type: "+post.getRBCA_roof_type()
                + "\n\n" + "Specify other: "+post.getRBCA_roof_type_oth()
                + "\n\n" + "Roofing Material: "+post.getRBCA_roof_mat()
                + "\n\n" + "Specify other: "+post.getRBCA_roof_mat_oth()
                + "\n\n" + "Roof Damage: "+post.getRBCA_roof()
                + "\n\n" + "Roof Notes: "+post.getRBCA_roof_notes()
                + "\n\n" + "Interior Condition: "+post.getRBCA_int_cond()
                + "\n\n" + "Collections Present: "+post.getRBCA_int_collect_extant()
                + "\n\n" + "Collection Type: "+post.getRBCA_int_collect_type()
                + "\n\n" + "Specify Other: "+post.getRBCA_int_collect_type_oth()
                + "\n\n" + "Interior Notes: "+post.getRBCA_int_notes()
                + "\n\n" + "Landscape Features: "+post.getRBCA_landveg_feat()
                + "\n\n" + "Specify Other: "+post.getRBCA_landveg_feat_oth()
                + "\n\n" + "Landscape Damage: "+post.getRBCA_landveg()
                + "\n\n" + "Landscape Notes: "+post.getRBCA_landveg_notes()
                + "\n\n" + "Landscape Features: "+post.getRBCA_landblt_feat()
                + "\n\n" + "Specify Other: "+post.getRBCA_landblt_feat_oth()
                + "\n\n" + "Landscape Damage: "+post.getRBCA_landblt()
                + "\n\n" + "Landscape Notes: "+post.getRBCA_landblt_notes());
        
        

        String htmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"webview.css\" /></head><body><div id=\"container\">"
                + html + "</div></body></html>";
        webView.loadDataWithBaseURL("file:///android_asset/", htmlText,
                "text/html", "utf-8", null);

        if (post.isLocalDraft()) {
            shareURLButton.setVisibility(View.GONE);
            viewPostButton.setVisibility(View.GONE);
            addCommentButton.setVisibility(View.GONE);
        } else {
            shareURLButton.setVisibility(View.VISIBLE);
            viewPostButton.setVisibility(View.VISIBLE);
            if (post.isMt_allow_comments()) {
                addCommentButton.setVisibility(View.VISIBLE);
            } else {
                addCommentButton.setVisibility(View.GONE);
            }
        }

    }

    public interface OnDetailPostActionListener {
        public void onDetailPostAction(int action, Post post);
    }

    public void clearContent() {
        TextView title = (TextView) getActivity().findViewById(R.id.postTitle);
        title.setText("");
        WebView webView = (WebView) getActivity().findViewById(
                R.id.viewPostWebView);
        TextView tv = (TextView) getActivity().findViewById(
                R.id.viewPostTextView);
        tv.setText("");
        String htmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"webview.css\" /></head><body><div id=\"container\"></div></body></html>";
        webView.loadDataWithBaseURL("file:///android_asset/", htmlText,
                "text/html", "utf-8", null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState.isEmpty()) {
            outState.putBoolean("bug_19917_fix", true);
        }
        super.onSaveInstanceState(outState);
    }

}
