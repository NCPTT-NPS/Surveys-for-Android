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
        
        TextView m_coord_loc = (TextView) getActivity().findViewById(R.id.rbca_coord_loc);
        TextView m_coord_loc_oth = (TextView) getActivity().findViewById(R.id.rbca_coord_loc_oth);
        TextView m_coord_corner = (TextView) getActivity().findViewById(R.id.rbca_coord_corner);
        TextView m_coord_notes = (TextView) getActivity().findViewById(R.id.rbca_coord_notes);
        TextView m_addr_no = (TextView) getActivity().findViewById(R.id.rbca_addr_no);
        TextView m_addr_street = (TextView) getActivity().findViewById(R.id.rbca_addr_street);
        TextView m_bldg_area = (TextView) getActivity().findViewById(R.id.rbca_bldg_area);
        TextView m_bldg_posting = (TextView) getActivity().findViewById(R.id.rbca_bldg_posting);
        TextView m_bldg_posting_oth = (TextView) getActivity().findViewById(R.id.rbca_bldg_posting_oth);
        TextView m_bldg_occucy = (TextView) getActivity().findViewById(R.id.rbca_bldg_occucy);
        TextView m_bldg_occu_avail = (TextView) getActivity().findViewById(R.id.rbca_bldg_occu_avail);
        TextView m_bldg_stories = (TextView) getActivity().findViewById(R.id.rbca_bldg_stories);
        TextView m_bldg_width = (TextView) getActivity().findViewById(R.id.rbca_bldg_width);
        TextView m_bldg_length = (TextView) getActivity().findViewById(R.id.rbca_bldg_length);
        TextView m_bldg_use = (TextView) getActivity().findViewById(R.id.rbca_bldg_use);
        TextView m_bldg_use_oth = (TextView) getActivity().findViewById(R.id.rbca_bldg_use_oth);
        TextView m_bldg_outbldg = (TextView) getActivity().findViewById(R.id.rbca_bldg_outbldg);
        TextView m_bldg_outbldg_notes = (TextView) getActivity().findViewById(R.id.rbca_bldg_outbldg_notes);
        TextView m_bldg_units_res = (TextView) getActivity().findViewById(R.id.rbca_bldg_units_res);
        TextView m_bldg_units_comm = (TextView) getActivity().findViewById(R.id.rbca_bldg_units_comm);
        TextView m_bldg_occu_name = (TextView) getActivity().findViewById(R.id.rbca_bldg_occu_name);
        TextView m_bldg_occu_phone = (TextView) getActivity().findViewById(R.id.rbca_bldg_occu_phone);
        TextView m_bldg_notes = (TextView) getActivity().findViewById(R.id.rbca_bldg_notes);
        TextView m_hist_desig = (TextView) getActivity().findViewById(R.id.rbca_hist_desig);
        TextView m_hist_desig_oth = (TextView) getActivity().findViewById(R.id.rbca_hist_desig_oth);
        TextView m_hist_dist = (TextView) getActivity().findViewById(R.id.rbca_hist_dist);
        TextView m_hist_dist_name = (TextView) getActivity().findViewById(R.id.rbca_hist_dist_name);
        TextView m_hist_appear = (TextView) getActivity().findViewById(R.id.rbca_hist_appear);
        TextView m_hist_age = (TextView) getActivity().findViewById(R.id.rbca_hist_age);
        TextView m_hist_age_meta = (TextView) getActivity().findViewById(R.id.rbca_hist_age_meta);
        TextView m_hist_yr_built = (TextView) getActivity().findViewById(R.id.rbca_hist_yr_built);
        TextView m_hist_age_src = (TextView) getActivity().findViewById(R.id.rbca_hist_age_src);
        TextView m_hist_age_src_oth = (TextView) getActivity().findViewById(R.id.rbca_hist_age_src_oth);
        TextView m_hist_notes = (TextView) getActivity().findViewById(R.id.rbca_hist_notes);
        TextView m_dmg_source = (TextView) getActivity().findViewById(R.id.rbca_dmg_source);
        TextView m_dmg_source_oth = (TextView) getActivity().findViewById(R.id.rbca_dmg_source_oth);
        TextView m_dmg_total = (TextView) getActivity().findViewById(R.id.rbca_dmg_total);
        TextView m_dmg_desc = (TextView) getActivity().findViewById(R.id.rbca_dmg_desc);
        TextView m_struct_type = (TextView) getActivity().findViewById(R.id.rbca_struct_type);
        TextView m_struct_type_oth = (TextView) getActivity().findViewById(R.id.rbca_struct_type_oth);
        TextView m_struct_defects = (TextView) getActivity().findViewById(R.id.rbca_struct_defects);
        TextView m_struct = (TextView) getActivity().findViewById(R.id.rbca_struct);
        TextView m_struct_notes = (TextView) getActivity().findViewById(R.id.rbca_struct_notes);
        TextView m_found_type = (TextView) getActivity().findViewById(R.id.rbca_found_type);
        TextView m_found_type_oth = (TextView) getActivity().findViewById(R.id.rbca_found_type_oth);
        TextView m_found = (TextView) getActivity().findViewById(R.id.rbca_found);
        TextView m_found_notes = (TextView) getActivity().findViewById(R.id.rbca_found_notes);
        TextView m_extwall_mat = (TextView) getActivity().findViewById(R.id.rbca_extwall_mat);
        TextView m_extwall_mat_oth = (TextView) getActivity().findViewById(R.id.rbca_extwall_mat_oth);
        TextView m_extwall = (TextView) getActivity().findViewById(R.id.rbca_extwall);
        TextView m_extwall_notes = (TextView) getActivity().findViewById(R.id.rbca_extwall_notes);
        TextView m_extfeat_type = (TextView) getActivity().findViewById(R.id.rbca_extfeat_type);
        TextView m_extfeat_type_oth = (TextView) getActivity().findViewById(R.id.rbca_extfeat_type_oth);
        TextView m_extfeat = (TextView) getActivity().findViewById(R.id.rbca_extfeat);
        TextView m_extfeat_notes = (TextView) getActivity().findViewById(R.id.rbca_extfeat_notes);
        TextView m_win_type = (TextView) getActivity().findViewById(R.id.rbca_win_type);
        TextView m_win_type_oth = (TextView) getActivity().findViewById(R.id.rbca_win_type_oth);
        TextView m_win_mat = (TextView) getActivity().findViewById(R.id.rbca_win_mat);
        TextView m_win_mat_oth = (TextView) getActivity().findViewById(R.id.rbca_win_mat_oth);
        TextView m_win = (TextView) getActivity().findViewById(R.id.rbca_win);
        TextView m_win_notes = (TextView) getActivity().findViewById(R.id.rbca_win_notes);
        TextView m_roof_type = (TextView) getActivity().findViewById(R.id.rbca_roof_type);
        TextView m_roof_type_oth = (TextView) getActivity().findViewById(R.id.rbca_roof_type_oth);
        TextView m_roof_mat = (TextView) getActivity().findViewById(R.id.rbca_roof_mat);
        TextView m_roof_mat_oth = (TextView) getActivity().findViewById(R.id.rbca_roof_mat_oth);
        TextView m_roof = (TextView) getActivity().findViewById(R.id.rbca_roof);
        TextView m_roof_notes = (TextView) getActivity().findViewById(R.id.rbca_roof_notes);
        TextView m_int_cond = (TextView) getActivity().findViewById(R.id.rbca_int_cond);
        TextView m_int_collect_extant = (TextView) getActivity().findViewById(R.id.rbca_int_collect_extant);
        TextView m_int_collect_type = (TextView) getActivity().findViewById(R.id.rbca_int_collect_type);
        TextView m_int_collect_type_oth = (TextView) getActivity().findViewById(R.id.rbca_int_collect_type_oth);
        TextView m_int_notes = (TextView) getActivity().findViewById(R.id.rbca_int_notes);
        TextView m_landveg_feat = (TextView) getActivity().findViewById(R.id.rbca_landveg_feat);
        TextView m_landveg_feat_oth = (TextView) getActivity().findViewById(R.id.rbca_landveg_feat_oth);
        TextView m_landveg = (TextView) getActivity().findViewById(R.id.rbca_landveg);
        TextView m_landveg_notes = (TextView) getActivity().findViewById(R.id.rbca_landveg_notes);
        TextView m_landblt_feat = (TextView) getActivity().findViewById(R.id.rbca_landblt_feat);
        TextView m_landblt_feat_oth = (TextView) getActivity().findViewById(R.id.rbca_landblt_feat_oth);
        TextView m_landblt = (TextView) getActivity().findViewById(R.id.rbca_landblt);
        TextView m_landblt_notes = (TextView) getActivity().findViewById(R.id.rbca_landblt_notes);
        TextView m_hzrd = (TextView) getActivity().findViewById(R.id.rbca_hzrd);
        TextView m_hzrd_type = (TextView) getActivity().findViewById(R.id.rbca_hzrd_type);
        TextView m_hzrd_type_oth = (TextView) getActivity().findViewById(R.id.rbca_hzrd_type_oth);
        TextView m_hzrd_notes = (TextView) getActivity().findViewById(R.id.rbca_hzrd_notes);
        TextView m_hzrd_hazmat = (TextView) getActivity().findViewById(R.id.rbca_hzrd_hazmat);
        TextView m_hzrd_hazmat_oth = (TextView) getActivity().findViewById(R.id.rbca_hzrd_hazmat_oth);
        TextView m_actn = (TextView) getActivity().findViewById(R.id.rbca_actn);
        TextView m_actn_oth = (TextView) getActivity().findViewById(R.id.rbca_actn_oth);
        TextView m_eval = (TextView) getActivity().findViewById(R.id.rbca_eval);
        TextView m_eval_oth = (TextView) getActivity().findViewById(R.id.rbca_eval_oth);
        
        
        m_coord_loc.setText(post.getRBCA_coord_loc());
        m_coord_loc_oth.setText(post.getRBCA_coord_loc_oth());
        m_coord_corner.setText(post.getRBCA_coord_corner());
        m_coord_notes.setText(post.getRBCA_coord_notes());
        m_addr_no.setText(post.getRBCA_addr_no());
        m_addr_street.setText(post.getRBCA_addr_street());
        m_bldg_area.setText(post.getRBCA_bldg_area());
        m_bldg_posting.setText(post.getRBCA_bldg_posting());
        m_bldg_posting_oth.setText(post.getRBCA_bldg_posting_oth());
        m_bldg_occucy.setText(post.getRBCA_bldg_occucy());
        //m_bldg_occu_avail.setText(post.getRBCA_bldg_occucy_avail());
        m_bldg_stories.setText(Double.toString(post.getRBCA_bldg_stories()));
        m_bldg_width.setText(Double.toString(post.getRBCA_bldg_width()));
        m_bldg_length.setText(Double.toString(post.getRBCA_bldg_length()));
        m_bldg_use.setText(post.getRBCA_bldg_use());
        m_bldg_use_oth.setText(post.getRBCA_bldg_use_oth());
        //m_bldg_outbldg.setText(post.getRBCA_bldg_outbldg());
        m_bldg_outbldg_notes.setText(post.getRBCA_bldg_outbldg_notes());
        //m_bldg_units_res.setText(post.getRBCA_bldg_units_res());
        //m_bldg_units_comm.setText(post.getRBCA_bldg_units_comm());
        m_bldg_occu_name.setText(post.getRBCA_bldg_occu_name());
        //m_bldg_occu_phone.setText(post.getRBCA_bldg_occu_phone());
        m_bldg_notes.setText(post.getRBCA_bldg_notes());
        m_hist_desig.setText(post.getRBCA_hist_desig());
        m_hist_desig_oth.setText(post.getRBCA_hist_desig_oth());
        m_hist_dist.setText(post.getRBCA_hist_dist());
        m_hist_dist_name.setText(post.getRBCA_hist_dist_name());
        //m_hist_appear.setText(post.getRBCA_hist_appear());
        //m_hist_age.setText(post.getRBCA_hist_age());
        m_hist_age_meta.setText(post.getRBCA_hist_age_meta());
        //m_hist_yr_built.setText(post.getRBCA_hist_yr_built());
        //m_hist_age_src.setText(post.getRBCA_hist_age_src());
        m_hist_age_src_oth.setText(post.getRBCA_hist_age_src_oth());
        m_hist_notes.setText(post.getRBCA_hist_notes());
        m_dmg_source.setText(post.getRBCA_dmg_source());
        m_dmg_source_oth.setText(post.getRBCA_dmg_source_oth());
        //m_dmg_total.setText(post.getRBCA_dmg_total());
        m_dmg_desc.setText(post.getRBCA_dmg_desc());
        m_struct_type.setText(post.getRBCA_struct_type());
        m_struct_type_oth.setText(post.getRBCA_struct_type_oth());
        m_struct_defects.setText(post.getRBCA_struct_defects());
        //m_struct.setText(post.getRBCA_struct());
        m_struct_notes.setText(post.getRBCA_struct_notes());
        m_found_type.setText(post.getRBCA_found_type());
        m_found_type_oth.setText(post.getRBCA_found_type_oth());
        //m_found.setText(post.getRBCA_found());
        m_found_notes.setText(post.getRBCA_found_notes());
        m_extwall_mat.setText(post.getRBCA_extwall_mat());
        m_extwall_mat_oth.setText(post.getRBCA_extwall_mat_oth());
        //m_extwall.setText(post.getRBCA_extwall());
        m_extwall_notes.setText(post.getRBCA_extwall_notes());
        m_extfeat_type.setText(post.getRBCA_extfeat_type());
        m_extfeat_type_oth.setText(post.getRBCA_extfeat_type_oth());
        //m_extfeat.setText(post.getRBCA_extfeat());
        m_extfeat_notes.setText(post.getRBCA_extfeat_notes());
        m_win_type.setText(post.getRBCA_win_type());
        m_win_type_oth.setText(post.getRBCA_win_type_oth());
        m_win_mat.setText(post.getRBCA_win_mat());
        m_win_mat_oth.setText(post.getRBCA_win_mat_oth());
        //m_win.setText(post.getRBCA_win());
        m_win_notes.setText(post.getRBCA_win_notes());
        m_roof_type.setText(post.getRBCA_roof_type());
        m_roof_type_oth.setText(post.getRBCA_roof_type_oth());
        m_roof_mat.setText(post.getRBCA_roof_mat());
        m_roof_mat_oth.setText(post.getRBCA_roof_mat_oth());
        //m_roof.setText(post.getRBCA_roof());
        m_roof_notes.setText(post.getRBCA_roof_notes());
        m_int_cond.setText(post.getRBCA_int_cond());
        //m_int_collect_extant.setText(post.getRBCA_int_collect_extant());
        m_int_collect_type.setText(post.getRBCA_int_collect_type());
        m_int_collect_type_oth.setText(post.getRBCA_int_collect_type_oth());
        m_int_notes.setText(post.getRBCA_int_notes());
        m_landveg_feat.setText(post.getRBCA_landveg_feat());
        m_landveg_feat_oth.setText(post.getRBCA_landveg_feat_oth());
        //m_landveg.setText(post.getRBCA_landveg());
        m_landveg_notes.setText(post.getRBCA_landveg_notes());
        m_landblt_feat.setText(post.getRBCA_landblt_feat());
        m_landblt_feat_oth.setText(post.getRBCA_landblt_feat_oth());
        //m_landblt.setText(post.getRBCA_landblt());
        m_landblt_notes.setText(post.getRBCA_landblt_notes());
        //m_hzrd.setText(post.getRBCA_hzrd());
        m_hzrd_type.setText(post.getRBCA_hzrd_type());
        m_hzrd_type_oth.setText(post.getRBCA_hzrd_type_oth());
        m_hzrd_notes.setText(post.getRBCA_hzrd_notes());
        m_hzrd_hazmat.setText(post.getRBCA_hzrd_hazmat());
        m_hzrd_hazmat_oth.setText(post.getRBCA_hzrd_hazmat_oth());
        m_actn.setText(post.getRBCA_actn());
        m_actn_oth.setText(post.getRBCA_actn_oth());
        m_eval.setText(post.getRBCA_eval());
        m_eval_oth.setText(post.getRBCA_eval_oth());
        

//        WebView webView = (WebView) getActivity().findViewById(
//                R.id.viewPostWebView);
//        TextView tv = (TextView) getActivity().findViewById(
//                R.id.viewPostTextView);
        ImageButton shareURLButton = (ImageButton) getActivity().findViewById(
                R.id.sharePostLink);
        ImageButton viewPostButton = (ImageButton) getActivity().findViewById(
                R.id.viewPost);
        ImageButton addCommentButton = (ImageButton) getActivity().findViewById(
                R.id.addComment);

//        tv.setVisibility(View.GONE);
//        webView.setVisibility(View.VISIBLE);
//        String html = StringHelper.addPTags(post.getDescription()
//                + "\n\n" + post.getMt_text_more() 
//                + "\n\n" + "Coordinate Location: "+post.getRBCA_coord_loc()
//                + "\n\n" + "Coordinate Location Other: "+post.getRBCA_coord_loc_oth()
//                + "\n\n" + "Coordinate Corner: "+post.getRBCA_coord_corner()
//                + "\n\n" + "Coordinate Notes: "+post.getRBCA_coord_notes()
//                + "\n\n" + "Address Number: "+post.getRBCA_addr_no()
//                + "\n\n" + "Address Street: "+post.getRBCA_addr_street()
//                + "\n\n" + "Area Assessed: "+post.getRBCA_bldg_area()
//                + "\n\n" + "Posting: "+post.getRBCA_bldg_posting()
//                + "\n\n" + "Posting Other: "+post.getRBCA_bldg_posting_oth()
//                + "\n\n" + "Occupancy: "+post.getRBCA_bldg_occucy() 
//                + "\n\n" + "Occupancy Available: "+post.getRBCA_bldg_occucy_avail()
//                + "\n\n" + "# of Stories: "+post.getRBCA_bldg_stories() 
//                + "\n\n" + "Width: "+post.getRBCA_bldg_width()
//                + "\n\n" + "Length: "+post.getRBCA_bldg_length()
//                + "\n\n" + "Use(s): "+post.getRBCA_bldg_use()
//                + "\n\n" + "Uses_other: "+post.getRBCA_bldg_use_oth()
//                + "\n\n" + "Outbuildings: "+post.getRBCA_bldg_outbldg()
//                + "\n\n" + "Outbuildings notes: "+post.getRBCA_bldg_outbldg_notes()
//                + "\n\n" + "# Residential Units: "+post.getRBCA_bldg_units_res()
//                + "\n\n" + "# Commercial Units: "+post.getRBCA_bldg_units_comm()
//                + "\n\n" + "Occupant Name: "+post.getRBCA_bldg_occu_name()
//                + "\n\n" + "Occupant Phone: "+post.getRBCA_bldg_occu_phone()
//                + "\n\n" + "Notes: "+post.getRBCA_bldg_notes()
//                + "\n\n" + "Historic Designation: "+post.getRBCA_hist_desig()
//                + "\n\n" + "Hist Designation Other: "+post.getRBCA_hist_desig_oth()
//                + "\n\n" + "Located Historic District?: "+post.getRBCA_hist_dist()
//                + "\n\n" + "Historic Distrit Name: "+post.getRBCA_hist_dist_name()
//                + "\n\n" + "Potentially Eligible: "+post.getRBCA_hist_appear()
//                + "\n\n" + "Building Age: "+post.getRBCA_hist_age()
//                + "\n\n" + "Age Is: "+post.getRBCA_hist_age_meta()
//                + "\n\n" + "Actual Year Built: "+post.getRBCA_hist_yr_built()
//                + "\n\n" + "Age Source: "+post.getRBCA_hist_age_src()
//                + "\n\n" + "Other: "+post.getRBCA_hist_age_src_oth()
//                + "\n\n" + "Notes: "+post.getRBCA_hist_notes()
//                + "\n\n" + "Damage Source: "+post.getRBCA_dmg_source()
//                + "\n\n" + "Damage Source Oth: "+post.getRBCA_dmg_source_oth()
//                + "\n\n" + "Damage Total: "+post.getRBCA_dmg_total()
//                + "\n\n" + "Damage Description: "+post.getRBCA_dmg_desc()
//                + "\n\n" + "Type of Structure: "+post.getRBCA_struct_type()
//                + "\n\n" + "Specify Other: "+post.getRBCA_struct_type_oth()
//                + "\n\n" + "Structural Defects: "+post.getRBCA_struct_defects()
//                + "\n\n" + "Structure Damage: "+post.getRBCA_struct()
//                + "\n\n" + "Structure Notes: "+post.getRBCA_struct_notes()
//                + "\n\n" + "Foundation type: "+post.getRBCA_found_type()
//                + "\n\n" + "Specify Other: "+post.getRBCA_found_type_oth()
//                + "\n\n" + "Foundation Damage: "+post.getRBCA_found()
//                + "\n\n" + "Foundation Notes: "+post.getRBCA_found_notes()
//                + "\n\n" + "Exterior Wall Materials: "+post.getRBCA_extwall_mat()
//                + "\n\n" + "Specify Other: "+post.getRBCA_extwall_mat_oth()
//                + "\n\n" + "Exterior Wall Damage: "+post.getRBCA_extwall()
//                + "\n\n" + "Exterior Wall Notes: "+post.getRBCA_extwall_notes()
//                + "\n\n" + "Exterior Features Type: "+post.getRBCA_extfeat_type()
//                + "\n\n" + "Specify Other: "+post.getRBCA_extfeat_type_oth()
//                + "\n\n" + "Exterior Features Damage: "+post.getRBCA_extfeat()
//                + "\n\n" + "Exterior Features Notes: "+post.getRBCA_extfeat_notes()
//                + "\n\n" + "Window Type: "+post.getRBCA_win_type()
//                + "\n\n" + "Specify Other: "+post.getRBCA_win_type_oth()
//                + "\n\n" + "Window Sash Material: "+post.getRBCA_win_mat()
//                + "\n\n" + "Specify other: "+post.getRBCA_win_type_oth()
//                + "\n\n" + "Window Damage: "+post.getRBCA_win()
//                + "\n\n" + "Window Notes: "+post.getRBCA_win_notes()
//                + "\n\n" + "Roof Type: "+post.getRBCA_roof_type()
//                + "\n\n" + "Specify other: "+post.getRBCA_roof_type_oth()
//                + "\n\n" + "Roofing Material: "+post.getRBCA_roof_mat()
//                + "\n\n" + "Specify other: "+post.getRBCA_roof_mat_oth()
//                + "\n\n" + "Roof Damage: "+post.getRBCA_roof()
//                + "\n\n" + "Roof Notes: "+post.getRBCA_roof_notes()
//                + "\n\n" + "Interior Condition: "+post.getRBCA_int_cond()
//                + "\n\n" + "Collections Present: "+post.getRBCA_int_collect_extant()
//                + "\n\n" + "Collection Type: "+post.getRBCA_int_collect_type()
//                + "\n\n" + "Specify Other: "+post.getRBCA_int_collect_type_oth()
//                + "\n\n" + "Interior Notes: "+post.getRBCA_int_notes()
//                + "\n\n" + "Landscape Features: "+post.getRBCA_landveg_feat()
//                + "\n\n" + "Specify Other: "+post.getRBCA_landveg_feat_oth()
//                + "\n\n" + "Landscape Damage: "+post.getRBCA_landveg()
//                + "\n\n" + "Landscape Notes: "+post.getRBCA_landveg_notes()
//                + "\n\n" + "Landscape Features: "+post.getRBCA_landblt_feat()
//                + "\n\n" + "Specify Other: "+post.getRBCA_landblt_feat_oth()
//                + "\n\n" + "Landscape Damage: "+post.getRBCA_landblt()
//                + "\n\n" + "Landscape Notes: "+post.getRBCA_landblt_notes())
//                + "\n\n" + "Are there threats?: "+post.getRBCA_hzrd()
//                + "\n\n" + "Hazards: "+post.getRBCA_hzrd_type()
//                + "\n\n" + "Other: "+post.getRBCA_hzrd_type_oth()
//                + "\n\n" + "Hazard Description: "+post.getRBCA_hzrd_notes()
//                + "\n\n" + "Hazardous Materials: "+post.getRBCA_hzrd_hazmat()
//                + "\n\n" + "Other: "+post.getRBCA_hzrd_hazmat_oth()
//                + "\n\n" + "Recommended Actions: "+post.getRBCA_actn()
//                + "\n\n" + "Other: "+post.getRBCA_actn_oth()
//                + "\n\n" + "Recommended Evaluations: "+post.getRBCA_eval()
//                + "\n\n" + "Other: "+post.getRBCA_eval_oth();
//        
//        
//
//        String htmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"webview.css\" /></head><body><div id=\"container\">"
//                + html + "</div></body></html>";
//        webView.loadDataWithBaseURL("file:///android_asset/", htmlText,
//                "text/html", "utf-8", null);

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
//        WebView webView = (WebView) getActivity().findViewById(
//                R.id.viewPostWebView);
//        TextView tv = (TextView) getActivity().findViewById(
//                R.id.viewPostTextView);
//        tv.setText("");
//        String htmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"webview.css\" /></head><body><div id=\"container\"></div></body></html>";
//        webView.loadDataWithBaseURL("file:///android_asset/", htmlText,
//                "text/html", "utf-8", null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState.isEmpty()) {
            outState.putBoolean("bug_19917_fix", true);
        }
        super.onSaveInstanceState(outState);
    }

}
