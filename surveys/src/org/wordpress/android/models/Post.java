package org.wordpress.android.models;

import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;

import org.wordpress.android.WordPress;

public class Post {

    private long id;
    private int blogID;
    private String categories;
    private String custom_fields;
    private long dateCreated;
    private long date_created_gmt;
    private String description;
    private String link;
    private boolean mt_allow_comments;
    private boolean mt_allow_pings;
    private String mt_excerpt;
    private String mt_keywords;
    private String mt_text_more;
    private String permaLink;
    private String post_status;
    private String postid;
    private String title;
    private String userid;
    
    private String wp_author_display_name;
    private String wp_author_id;
    private String wp_password;
    private String wp_post_format;
    private String wp_slug;
    private boolean localDraft;
    private boolean uploaded;
    private double latitude;
    private double longitude;
    private boolean isPage;
    private boolean isLocalChange;
    
    //Fields added MoCAs for Android
    private String rbca_coord_loc,rbca_bldg_use,rbca_bldg_use_oth, rbca_bldg_outbldg_notes,rbca_bldg_occu_name,rbca_bldg_notes;
    private double rbca_coord_altitude, rbca_coord_accuracy, rbca_bldg_stories, rbca_bldg_width, rbca_bldg_length, rbca_coord_longitude,rbca_coord_latitude;
    private String rbca_coord_notes, rbca_addr_no, rbca_addr_street, rbca_bldg_area, rbca_bldg_posting;
    private String rbca_bldg_posting_oth, rbca_coord_loc_oth,rbca_coord_corner,rbca_bldg_occucy, rbca_hist_desig, rbca_hist_desig_oth;
    private String rbca_hist_dist, rbca_hist_dist_name, rbca_hist_age_meta, rbca_dmg_source, rbca_dmg_source_oth,rbca_dmg_desc;
    private int rbca_bldg_occucy_avail, rbca_bldg_outbldg,rbca_bldg_units_res, rbca_bldg_units_comm, rbca_bldg_occu_phone;
    private int rbca_hist_appear, rbca_hist_age,rbca_hist_yr_built, rbca_dmg_total,rbca_struct,rbca_found,rbca_extwall,rbca_extfeat;
    private String rbca_struct_type, rbca_hist_age_src_oth, rbca_hist_notes, rbca_struct_defects;
    private String rbca_struct_type_oth,rbca_struct_notes,rbca_found_type,rbca_found_type_oth,rbca_found_notes,rbca_extwall_mat,rbca_extwall_mat_oth;
    private String rbca_extwall_notes,rbca_extfeat_type,rbca_extfeat_type_oth, rbca_extfeat_notes,rbca_win_type,rbca_win_type_oth,rbca_win_mat,rbca_win_mat_oth;
    private int rbca_win,rbca_roof,rbca_int_collect_extant,rbca_landveg,rbca_landblt, rbca_hzrd, rbca_hist_age_src;
    private String rbca_win_notes,rbca_roof_type,rbca_roof_type_oth,rbca_roof_mat,rbca_roof_mat_oth,rbca_roof_notes,rbca_int_cond;
    private String rbca_int_collect_type,rbca_int_collect_type_oth,rbca_int_notes,rbca_landveg_feat,rbca_landveg_feat_oth,rbca_landveg_notes ;
    private String rbca_landblt_feat, rbca_landblt_feat_oth,rbca_landblt_notes, rbca_hzrd_type, rbca_hzrd_type_oth, rbca_hzrd_notes,rbca_hzrd_hazmat;
    private String rbca_hzrd_hazmat_oth, rbca_actn, rbca_actn_oth, rbca_eval, rbca_eval_oth;
    private String rbca_asser_name, rbca_asser_org, rbca_asser_email, rbca_bldg_name, rbca_bldg_is_extant, rbca_addr_notes,rbca_asser_phone;
    private String rbca_bldg_posting_img,rbca_int_img1,rbca_int_desc1,rbca_int_img2, rbca_int_desc2, rbca_int_img3, rbca_int_desc3;
    private String rbca_media_img1,rbca_media_desc1,rbca_media_img2,rbca_media_desc2,rbca_media_img3,rbca_media_desc3, rbca_media_img4, rbca_media_desc4;
    private String rbca_media_img5, rbca_media_desc5, rbca_media_img6, rbca_media_desc6, rbca_img_right,rbca_img_front, rbca_img_left;
    
    //end of Field added MoCAs for Android
    

    private String mediaPaths;
    private String quickPostType;

    private Blog blog;

    public List<String> imageUrl = new Vector<String>();
    List<String> selectedCategories = new Vector<String>();

    public Post(int blog_id, long post_id, boolean isPage) {
        // load an existing post
        List<Object> postVals = WordPress.wpDB.loadPost(blog_id, isPage, post_id);
        
//        System.out.println("Post.java LoadPost. #elemtns :" +postVals.size());
//        for (int i=0;i<postVals.size();i++){
//            System.out.println("Linea "+ i +": "+postVals.get(i));
//        }
        
        if (postVals != null) {
            try {
                this.blog = new Blog(blog_id);
            } catch (Exception e) {
            }
            this.id = (Long) postVals.get(0);
            this.blogID = blog_id;
            if (postVals.get(2) != null)
                this.postid = postVals.get(2).toString();
            this.title = postVals.get(3).toString();
            this.dateCreated = (Long) postVals.get(4);
            this.date_created_gmt = (Long) postVals.get(5);
            this.categories = postVals.get(6).toString();
            this.custom_fields = postVals.get(7).toString();
            this.description = postVals.get(8).toString();
            this.link = postVals.get(9).toString();
            this.mt_allow_comments = (Integer) postVals.get(10) > 0;
            this.mt_allow_pings = (Integer) postVals.get(11) > 0;
            this.mt_excerpt = postVals.get(12).toString();
            this.mt_keywords = postVals.get(13).toString();
            if (postVals.get(14) != null)
                this.mt_text_more = postVals.get(14).toString();
            else
                this.mt_text_more = "";
            this.permaLink = postVals.get(15).toString();
            this.post_status = postVals.get(16).toString();
            this.userid = postVals.get(17).toString();
            this.wp_author_display_name = postVals.get(18).toString();
            this.wp_author_id = postVals.get(19).toString();
            this.wp_password = postVals.get(20).toString();
            this.wp_post_format = postVals.get(21).toString();
            this.wp_slug = postVals.get(22).toString();
            this.mediaPaths = postVals.get(23).toString();
            this.latitude = (Double) postVals.get(24);
            this.longitude = (Double) postVals.get(25);
            this.localDraft = (Integer) postVals.get(26) > 0;
            this.uploaded = (Integer) postVals.get(27) > 0;
            this.isPage = (Integer) postVals.get(28) > 0;
            
            //
            this.rbca_asser_name = postVals.get(29).toString();
            this.rbca_asser_org = postVals.get(30).toString();
            this.rbca_asser_email = postVals.get(31).toString();
            this.rbca_asser_phone = postVals.get(32).toString();
            this.rbca_coord_latitude = (Double) postVals.get(33);
            this.rbca_coord_longitude = (Double) postVals.get(34);
            this.rbca_coord_altitude = (Double) postVals.get(35);
            this.rbca_coord_accuracy = (Double) postVals.get(36);
            this.rbca_coord_loc = postVals.get(37).toString();
            this.rbca_coord_loc_oth = postVals.get(38).toString();
            this.rbca_coord_corner = postVals.get(39).toString();
            this.rbca_coord_notes = postVals.get(40).toString();
            this.rbca_addr_no = postVals.get(41).toString();
            this.rbca_addr_street = postVals.get(42).toString();
            this.rbca_addr_notes = postVals.get(43).toString();
            this.rbca_img_right = postVals.get(44).toString();
            this.rbca_img_front = postVals.get(45).toString();
            this.rbca_img_left = postVals.get(46).toString();
            this.rbca_bldg_name = postVals.get(47).toString();
            this.rbca_bldg_is_extant = postVals.get(48).toString();
            this.rbca_bldg_area = postVals.get(49).toString();
            this.rbca_bldg_posting = postVals.get(50).toString();
            this.rbca_bldg_posting_oth = postVals.get(51).toString();
            this.rbca_bldg_posting_img = postVals.get(52).toString();
            this.rbca_bldg_occucy = postVals.get(53).toString();
            this.rbca_bldg_occucy_avail = (Integer) postVals.get(54);
            this.rbca_bldg_stories = (Double) postVals.get(55);
            this.rbca_bldg_width = (Double) postVals.get(56);
            this.rbca_bldg_length = (Double) postVals.get(57);
            this.rbca_bldg_use = postVals.get(58).toString();
            this.rbca_bldg_use_oth = postVals.get(59).toString();
            this.rbca_bldg_outbldg = (Integer) postVals.get(60);
            this.rbca_bldg_outbldg_notes = postVals.get(61).toString();
            this.rbca_bldg_units_res = (Integer) postVals.get(62);
            this.rbca_bldg_units_comm = (Integer) postVals.get(63);
            this.rbca_bldg_occu_name = postVals.get(64).toString();
            this.rbca_bldg_occu_phone = (Integer) postVals.get(65);
            this.rbca_bldg_notes = postVals.get(66).toString();
            this.rbca_hist_desig = postVals.get(67).toString();
            this.rbca_hist_desig_oth = postVals.get(68).toString();
            this.rbca_hist_dist = postVals.get(69).toString();
            this.rbca_hist_dist_name = postVals.get(70).toString(); 
            this.rbca_hist_appear = (Integer) postVals.get(71);
            this.rbca_hist_age = (Integer) postVals.get(72);  
            this.rbca_hist_age_meta = postVals.get(73).toString();
            this.rbca_hist_yr_built = (Integer) postVals.get(74);
            this.rbca_hist_age_src = (Integer) postVals.get(75);
            this.rbca_hist_age_src_oth = postVals.get(76).toString();
            this.rbca_hist_notes = postVals.get(77).toString();
            this.rbca_dmg_source = postVals.get(78).toString();
            this.rbca_dmg_source_oth = postVals.get(79).toString();
            this.rbca_dmg_total = (Integer) postVals.get(80);
            this.rbca_dmg_desc = postVals.get(81).toString();
            this.rbca_struct_type = postVals.get(82).toString();
            this.rbca_struct_type_oth = postVals.get(83).toString();
            this.rbca_struct_defects = postVals.get(84).toString();
            this.rbca_struct = (Integer) postVals.get(85);
            this.rbca_struct_notes = postVals.get(86).toString();
            this.rbca_found_type = postVals.get(87).toString();
            this.rbca_found_type_oth = postVals.get(88).toString();
            this.rbca_found = (Integer) postVals.get(89);
            this.rbca_found_notes = postVals.get(90).toString();
            this.rbca_extwall_mat = postVals.get(91).toString();
            this.rbca_extwall_mat_oth = postVals.get(92).toString();
            this.rbca_extwall = (Integer) postVals.get(93);
            this.rbca_extwall_notes = postVals.get(94).toString();
            this.rbca_extfeat_type = postVals.get(95).toString();
            this.rbca_extfeat_type_oth = postVals.get(96).toString();
            this.rbca_extfeat = (Integer) postVals.get(97);
            this.rbca_extfeat_notes = postVals.get(98).toString();
            this.rbca_win_type = postVals.get(99).toString();
            this.rbca_win_type_oth = postVals.get(100).toString();
            this.rbca_win_mat = postVals.get(101).toString();
            this.rbca_win_mat_oth = postVals.get(102).toString();
            this.rbca_win = (Integer) postVals.get(103);
            this.rbca_win_notes = postVals.get(104).toString();
            this.rbca_roof_type = postVals.get(105).toString();
            this.rbca_roof_type_oth = postVals.get(106).toString();
            this.rbca_roof_mat = postVals.get(107).toString();
            this.rbca_roof_mat_oth = postVals.get(108).toString();
            this.rbca_roof = (Integer) postVals.get(109);
            this.rbca_roof_notes = postVals.get(110).toString();
            this.rbca_int_cond = postVals.get(111).toString();
            this.rbca_int_collect_extant = (Integer) postVals.get(112);
            this.rbca_int_collect_type = postVals.get(113).toString();
            this.rbca_int_collect_type_oth = postVals.get(114).toString();
            this.rbca_int_img1 = postVals.get(115).toString();
            this.rbca_int_desc1 = postVals.get(116).toString();
            this.rbca_int_img2 = postVals.get(116).toString();
            this.rbca_int_desc2 = postVals.get(117).toString();
            this.rbca_int_img3 = postVals.get(118).toString();
            this.rbca_int_desc3 = postVals.get(119).toString();
            this.rbca_int_notes = postVals.get(120).toString();
            this.rbca_landveg_feat = postVals.get(121).toString();
            this.rbca_landveg_feat_oth = postVals.get(122).toString();
            this.rbca_landveg = (Integer) postVals.get(123);
            this.rbca_landveg_notes = postVals.get(124).toString();
            this.rbca_landblt_feat = postVals.get(125).toString();
            this.rbca_landblt_feat_oth = postVals.get(126).toString();
            this.rbca_landblt = (Integer) postVals.get(127);
            this.rbca_landblt_notes = postVals.get(128).toString();
            this.rbca_media_img1 = postVals.get(129).toString();
            this.rbca_media_desc1 = postVals.get(130).toString();
            this.rbca_media_img2 = postVals.get(131).toString();
            this.rbca_media_desc2 = postVals.get(132).toString();
            this.rbca_media_img3 = postVals.get(133).toString();
            this.rbca_media_desc3 = postVals.get(134).toString();
            this.rbca_media_img4 = postVals.get(135).toString();
            this.rbca_media_desc4 = postVals.get(136).toString();
            this.rbca_media_img5 = postVals.get(137).toString();
            this.rbca_media_desc5 = postVals.get(138).toString();
            this.rbca_media_img6 = postVals.get(139).toString();
            this.rbca_media_desc6 = postVals.get(140).toString();
            this.rbca_hzrd = (Integer) postVals.get(141);
            this.rbca_hzrd_type = postVals.get(142).toString();
            this.rbca_hzrd_type_oth = postVals.get(143).toString();
            this.rbca_hzrd_notes = postVals.get(144).toString();
            this.rbca_hzrd_hazmat = postVals.get(145).toString();
            this.rbca_hzrd_hazmat_oth = postVals.get(146).toString();
            this.rbca_actn = postVals.get(147).toString();
            this.rbca_actn_oth = postVals.get(148).toString();
            this.rbca_eval = postVals.get(149).toString();
            this.rbca_eval_oth = postVals.get(150).toString();

            //
            this.isLocalChange = (Integer) postVals.get(151) > 0;
            
            
        } else {
            this.id = -1;
        }
    }

    public Post(int blog_id, String title, String content, String picturePaths, long date, String categories, String tags, String status,
            String password, double latitude, double longitude, boolean isPage, String postFormat, boolean createBlogReference, boolean isLocalChange, 
            
            String asser_name, String asser_org, String asser_email, String asser_phone,double coord_latitude, double coord_longitude, double coord_altitude,
            double coord_accuracy, String rbca_coord_loc,String rbca_coord_loc_oth, String rbca_coord_corner, String rbca_coord_notes, String rbca_addr_no,
            String rbca_addr_street, String addr_notes, String img_right, String img_front, String img_left, String bldg_name, String bldg_is_extant,
            String rbca_bldg_area, String rbca_bldg_posting, String rbca_bldg_posting_oth, String bldg_posting_img, String rbca_bldg_occucy, int rbca_bldg_occucy_avail,
            double rbca_bldg_stories,double bldg_width, double bldg_length, String bldg_use, String bldg_use_oth, int rbca_bldg_outbldg, String outbldg_notes, 
            int units_res, int units_comm, String occu_name, int occu_phone, String occu_notes, String hist_desig, String hist_desig_oth, String hist_dist, 
            String hist_dist_name, int hist_appear, int hist_age, String hist_age_meta, int hist_yr_built,int hist_age_src, String hist_age_src_oth, String hist_notes,
            String dmg_source, String dmg_source_oth,int dmg_total,String dmg_desc,String struct_type,String struct_type_oth, String struct_defects, int struct, String struct_notes,
            String found_type, String found_type_oth, int found, String found_notes, String extwall_mat, String extwall_mat_oth, int extwall, String extwall_notes,
            String extfeat_type, String extfeat_type_oth, int extfeat, String extfeat_notes, String win_type, String win_type_oth, String win_mat, String win_mat_oth,
            int win, String win_notes, String roof_type, String roof_type_oth, String roof_mat, String roof_mat_oth, int roof, String roof_notes, String int_cond,
            int int_collect_extant, String int_collect_type, String int_collect_type_oth, String int_img1, String int_desc1, String int_img2, String int_desc2,
            String int_img3, String int_desc3, String int_notes, String landveg_feat, String landveg_feat_oth, int landveg, String landveg_notes,
            String landblt_feat, String landblt_feat_oth, int landblt, String landblt_notes, String media_img1, String media_desc1, 
            String media_img2, String media_desc2, String media_img3, String media_desc3, String media_img4, String media_desc4, 
            String media_img5, String media_desc5, String media_img6, String media_desc6, int hzrd, String hzrd_type, String hzrd_type_oth, 
            String hzrd_notes, String hzrd_hazmat, String hzrd_hazmat_oth, String actn, String actn_oth, String eval, String eval_oth) {
        // create a new post
        if (createBlogReference) {
            try {
                this.blog = new Blog(blog_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.blogID = blog_id;
        this.title = title;
        this.description = content;
        this.mediaPaths = picturePaths;
        this.date_created_gmt = date;
        this.categories = categories;
        this.mt_keywords = tags;
        this.post_status = status;
        this.wp_password = password;
        this.isPage = isPage;
        this.wp_post_format = postFormat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isLocalChange = isLocalChange;
        
        //added MoCA for Android 
        this.rbca_asser_name = asser_name;
        this.rbca_asser_org = asser_org;
        this.rbca_asser_email = asser_email;
        this.rbca_asser_phone = asser_phone;
        this.rbca_coord_latitude = coord_latitude;
        this.rbca_coord_longitude = coord_longitude;
        this.rbca_coord_altitude = coord_altitude;
        this.rbca_coord_accuracy = coord_accuracy;
        this.rbca_coord_loc = rbca_coord_loc;
        this.rbca_coord_loc_oth = rbca_coord_loc_oth;
        this.rbca_coord_corner = rbca_coord_corner;
        this.rbca_coord_notes = rbca_coord_notes;
        this.rbca_addr_no = rbca_addr_no;
        this.rbca_addr_street = rbca_addr_street;
        this.rbca_addr_notes = addr_notes;
        this.rbca_img_right = img_right;
        this.rbca_img_front = img_front;
        this.rbca_img_left = img_left;
        this.rbca_bldg_name = bldg_name;
        this.rbca_bldg_is_extant = bldg_is_extant;
        this.rbca_bldg_area = rbca_bldg_area;
        this.rbca_bldg_posting = rbca_bldg_posting;
        this.rbca_bldg_posting_oth = rbca_bldg_posting_oth;
        this.rbca_bldg_posting_img = bldg_posting_img;
        this.rbca_bldg_occucy = rbca_bldg_occucy;
        this.rbca_bldg_occucy_avail = rbca_bldg_occucy_avail;
        this.rbca_bldg_stories = rbca_bldg_stories;
        this.rbca_bldg_width = bldg_width;
        this.rbca_bldg_length = bldg_length; 
        this.rbca_bldg_use = bldg_use;
        this.rbca_bldg_use_oth = bldg_use_oth;
        this.rbca_bldg_outbldg = rbca_bldg_outbldg;
        this.rbca_bldg_outbldg_notes = outbldg_notes;
        this.rbca_bldg_units_res = units_res;
        this.rbca_bldg_units_comm = units_comm;
        this.rbca_bldg_occu_name = occu_name;
        this.rbca_bldg_occu_phone = occu_phone;
        this.rbca_bldg_notes = occu_notes;
        this.rbca_hist_desig = hist_desig;
        this.rbca_hist_desig_oth = hist_desig_oth;
        this.rbca_hist_dist = hist_dist;
        this.rbca_hist_dist_name = hist_dist_name;
        this.rbca_hist_appear = hist_appear;
        this.rbca_hist_age = hist_age;
        this.rbca_hist_age_meta = hist_age_meta;
        this.rbca_hist_yr_built = hist_yr_built;
        this.rbca_hist_age_src = hist_age_src;
        this.rbca_hist_age_src_oth = hist_age_src_oth;
        this.rbca_hist_notes = hist_notes;
        this.rbca_dmg_source = dmg_source;
        this.rbca_dmg_source_oth = dmg_source_oth;
        this.rbca_dmg_total = dmg_total;
        this.rbca_dmg_desc = dmg_desc;
        this.rbca_struct_type = struct_type;
        this.rbca_struct_type_oth = struct_type_oth;
        this.rbca_struct_defects = struct_defects;
        this.rbca_struct = struct;
        this.rbca_struct_notes = struct_notes;
        this.rbca_found_type = found_type;
        this.rbca_found_type_oth = found_type_oth;
        this.rbca_found = found;
        this.rbca_found_notes = found_notes;
        this.rbca_extwall_mat = extwall_mat;
        this.rbca_extwall_mat_oth = extwall_mat_oth;
        this.rbca_extwall = extwall;
        this.rbca_extwall_notes = extwall_notes;
        this.rbca_extfeat_type = extfeat_type;
        this.rbca_extfeat_type_oth = extfeat_type_oth;
        this.rbca_extfeat = extfeat;
        this.rbca_extfeat_notes = extfeat_notes;
        this.rbca_win_type = win_type;
        this.rbca_win_type_oth = win_type_oth;
        this.rbca_win_mat = win_mat;
        this.rbca_win_mat_oth = win_mat_oth;
        this.rbca_win = win;
        this.rbca_win_notes = win_notes;
        this.rbca_roof_type = roof_type;
        this.rbca_roof_type_oth = roof_type_oth;
        this.rbca_roof_mat = roof_mat;
        this.rbca_roof_mat_oth = roof_mat_oth;
        this.rbca_roof = roof;
        this.rbca_roof_notes = roof_notes;
        this.rbca_int_cond = int_cond;
        this.rbca_int_collect_extant = int_collect_extant;
        this.rbca_int_collect_type = int_collect_type;
        this.rbca_int_collect_type_oth = int_collect_type_oth;
        this.rbca_int_img1 = int_img1;
        this.rbca_int_desc1 = int_desc1;
        this.rbca_int_img2 = int_img2;
        this.rbca_int_desc2 = int_desc2;
        this.rbca_int_img3 = int_img3;
        this.rbca_int_desc3 = int_desc3;
        this.rbca_int_notes = int_notes;
        this.rbca_landveg_feat = landveg_feat;
        this.rbca_landveg_feat_oth = landveg_feat_oth;
        this.rbca_landveg = landveg;
        this.rbca_landveg_notes = landveg_notes;
        this.rbca_landblt_feat = landblt_feat;
        this.rbca_landblt_feat_oth = landblt_feat_oth;
        this.rbca_landblt = landblt;
        this.rbca_landblt_notes = landblt_notes;
        this.rbca_media_img1 = media_img1;
        this.rbca_media_desc1 = media_desc1;
        this.rbca_media_img2 = media_img2;
        this.rbca_media_desc2 = media_desc2;
        this.rbca_media_img3 = media_img3;
        this.rbca_media_desc3 = media_desc3;
        this.rbca_media_img4 = media_img4;
        this.rbca_media_desc4 = media_desc4;
        this.rbca_media_img5 = media_img5;
        this.rbca_media_desc5 = media_desc5;
        this.rbca_media_img6 = media_img6;
        this.rbca_media_desc6 = media_desc6;
        this.rbca_hzrd = hzrd;
        this.rbca_hzrd_type = hzrd_type;
        this.rbca_hzrd_type_oth = hzrd_type_oth;
        this.rbca_hzrd_notes = hzrd_notes;
        this.rbca_hzrd_hazmat = hzrd_hazmat;
        this.rbca_hzrd_hazmat_oth = hzrd_hazmat_oth;
        this.rbca_actn = actn;
        this.rbca_actn_oth = actn_oth;
        this.rbca_eval = eval;
        this.rbca_eval_oth = eval_oth;
        
        
        
    }

    public long getId() {
        return id;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDate_created_gmt() {
        return date_created_gmt;
    }

    public void setDate_created_gmt(long dateCreatedGmt) {
        date_created_gmt = dateCreatedGmt;
    }

    public int getBlogID() {
        return blogID;
    }

    public void setBlogID(int blogID) {
        this.blogID = blogID;
    }

    public boolean isLocalDraft() {
        return localDraft;
    }

    public void setLocalDraft(boolean localDraft) {
        this.localDraft = localDraft;
    }

    public JSONArray getCategories() {
        JSONArray jArray = null;
        if (categories == null)
            categories = "";
        try {
            jArray = new JSONArray(categories);
        } catch (JSONException e) {
        }
        return jArray;
    }

    public void setCategories(JSONArray categories) {
        this.categories = categories.toString();
    }

    public JSONArray getCustom_fields() {
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(custom_fields);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jArray;
    }

    public void setCustom_fields(JSONArray customFields) {
        custom_fields = customFields.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isMt_allow_comments() {
        return mt_allow_comments;
    }

    public void setMt_allow_comments(boolean mtAllowComments) {
        mt_allow_comments = mtAllowComments;
    }

    public boolean isMt_allow_pings() {
        return mt_allow_pings;
    }

    public void setMt_allow_pings(boolean mtAllowPings) {
        mt_allow_pings = mtAllowPings;
    }

    public String getMt_excerpt() {
        return mt_excerpt;
    }

    public void setMt_excerpt(String mtExcerpt) {
        mt_excerpt = mtExcerpt;
    }

    public String getMt_keywords() {
        if (mt_keywords == null)
            return "";
        else
            return mt_keywords;
    }

    public void setMt_keywords(String mtKeywords) {
        mt_keywords = mtKeywords;
    }

    public String getMt_text_more() {
        if (mt_text_more == null)
            return "";
        else
            return mt_text_more;
    }

    public void setMt_text_more(String mtTextMore) {
        mt_text_more = mtTextMore;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

    public String getPost_status() {
        return post_status;
    }

    public void setPost_status(String postStatus) {
        post_status = postStatus;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getWP_author_display_name() {
        return wp_author_display_name;
    }

    public void setWP_author_display_name(String wpAuthorDisplayName) {
        wp_author_display_name = wpAuthorDisplayName;
    }

    public String getWP_author_id() {
        return wp_author_id;
    }

    public void setWP_author_id(String wpAuthorId) {
        wp_author_id = wpAuthorId;
    }

    public String getWP_password() {
        return wp_password;
    }

    public void setWP_password(String wpPassword) {
        wp_password = wpPassword;
    }

    public String getWP_post_format() {
        return wp_post_format;
    }

    public void setWP_post_form(String wpPostForm) {
        wp_post_format = wpPostForm;
    }

    public String getWP_slug() {
        return wp_slug;
    }

    public void setWP_slug(String wpSlug) {
        wp_slug = wpSlug;
    }

    public String getMediaPaths() {
        return mediaPaths;
    }

    public void setMediaPaths(String mediaPaths) {
        this.mediaPaths = mediaPaths;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isPage() {
        return isPage;
    }

    public void setPage(boolean isPage) {
        this.isPage = isPage;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public boolean isLocalChange() {
        return isLocalChange;
    }

    public void setLocalChange(boolean isLocalChange) {
        this.isLocalChange = isLocalChange;
    }

    public boolean save() {
        long newPostID = WordPress.wpDB.savePost(this, this.blogID);

        if (newPostID >= 0 && this.isLocalDraft() && !this.isUploaded()) {
            this.id = newPostID;
            return true;
        }

        return false;
    }

    public boolean update() {
        int success = WordPress.wpDB.updatePost(this, this.blogID);

        return success > 0;
    }

    public void delete() {
        // deletes a post/page draft
        WordPress.wpDB.deletePost(this);
    }

    public void deleteMediaFiles() {
        WordPress.wpDB.deleteMediaFilesForPost(this);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setQuickPostType(String type) {
        this.quickPostType = type;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getQuickPostType() {
        return quickPostType;
    }
    
    
    
    ///////////////////////MoCA for Android
    
    public String getRBCA_asser_name(){
        return rbca_asser_name;
    }
    
    public void setRBCA_asser_name(String asser_name){
        this.rbca_asser_name = asser_name;
    }
    
    public String getRBCA_asser_org(){
        return rbca_asser_org;
    }
    
    public void setRBCA_asser_org(String asser_org){
        this.rbca_asser_org = asser_org;
    }
    
    public String getRBCA_asser_email(){
        return rbca_asser_email;
    }
    
    public void setRBCA_asser_email(String asser_email){
        this.rbca_asser_email = asser_email;
    }
    
    public String getRBCA_asser_phone(){
        return rbca_asser_phone;
    }
    
    public void setRBCA_asser_phone(String asser_phone){
        this.rbca_asser_phone = asser_phone;
    }
    
    public double getRBCA_coord_latitude(){
        return rbca_coord_latitude;
    }
    
    public void setRBCA_coord_latitude(double coord_latitude){
        this.rbca_coord_latitude = coord_latitude;
    }
    
    public double getRBCA_coord_longitude(){
        return rbca_coord_longitude;
    }
    
    public void setRBCA_coord_longitude(double coord_longitude){
        this.rbca_coord_longitude = coord_longitude;
    }
    
    public double getRBCA_coord_altitude(){
        return rbca_coord_altitude;
    }
    
    public void setRBCA_coord_altitude(double coord_altitude){
        this.rbca_coord_altitude = coord_altitude;
    }
    
    public double getRBCA_coord_accuracy(){
        return rbca_coord_accuracy;
    }
    
    public void setRBCA_coord_accuracy(double coord_accuracy){
        this.rbca_coord_accuracy = coord_accuracy;
    }
    
    public String getRBCA_coord_loc(){
        return rbca_coord_loc;
    }
    
    public void setRBCA_coord_loc(String coord_loc){
        this.rbca_coord_loc = coord_loc;
    }
    
    public String getRBCA_coord_loc_oth(){
        return rbca_coord_loc_oth;
    }
    
    public void setRBCA_coord_loc_oth(String coord_loc_oth){
        this.rbca_coord_loc_oth = coord_loc_oth; 
    }
    
    public String getRBCA_coord_corner(){
        return rbca_coord_corner;
    }
    
    public void setRBCA_coord_corner(String coord_loc_corner){
        this.rbca_coord_corner = coord_loc_corner; 
    }
    
    public String getRBCA_coord_notes(){
        return rbca_coord_notes;
    }
    
    public void setRBCA_coord_notes(String coord_notes){
        this.rbca_coord_notes = coord_notes;
    }
     
    public void setRBCA_addr_no(String addr_no){
        this.rbca_addr_no = addr_no;
    }
    
    public String getRBCA_addr_no(){
        return rbca_addr_no;
    }
    
    public void setRBCA_addr_street(String addr_street){
        this.rbca_addr_street = addr_street;
    }
    
    public String getRBCA_addr_street(){
        return rbca_addr_street;
    }
    
    public String getRBCA_addr_notes(){
        return rbca_addr_notes;
    }
    
    public String getRBCA_img_right(){
        return rbca_img_right;
    }
    
    public void setRBCA_img_right(String img_right){
        this.rbca_img_right = img_right;
    }
    
    public String getRBCA_img_front(){
        return rbca_img_front;
    }
    
    public void setRBCA_img_front(String img_front){
        this.rbca_img_front = img_front;
    }
    
    public String getRBCA_img_left(){
        return rbca_img_left;
    }
    
    public void setRBCA_img_left(String img_left){
        this.rbca_img_left = img_left;
    }
    
    public void setRBCA_addr_notes(String addr_notes){
        this.rbca_addr_notes = addr_notes;
    }
    
    public String getRBCA_bldg_name(){
        return rbca_bldg_name;
    }
    
    public void setRBCA_bldg_name(String bldg_name){
        this.rbca_bldg_name = bldg_name;
    }
    
    public String getRBCA_bldg_is_extant(){
        return rbca_bldg_is_extant;
    }
    
    public void setRBCA_bldg_is_extant(String bldg_is_extant){
        this.rbca_bldg_is_extant = bldg_is_extant;
    }
           
    public void setRBCA_bldg_area(String area){
        this.rbca_bldg_area = area;
    }
    
    public String getRBCA_bldg_area(){
        return rbca_bldg_area;
    }
    
    public void setRBCA_bldg_posting(String posting){
        this.rbca_bldg_posting = posting;
    }
    
    public String getRBCA_bldg_posting(){
        return rbca_bldg_posting;
    }
    
    public void setRBCA_bldg_posting_oth(String posting_oth){
        this.rbca_bldg_posting_oth = posting_oth;
    }
    
    public String getRBCA_bldg_posting_oth(){
        return rbca_bldg_posting_oth;
    }
    
    public String getRBCA_bldg_posting_img(){
        return rbca_bldg_posting_img;
    }
    
    public void setRBCA_bldg_posting_img(String bldg_posting_img){
        this.rbca_bldg_posting_img = bldg_posting_img;
    }
    
    public String getRBCA_bldg_occucy(){
        return rbca_bldg_occucy;
    }
    public void setRBCA_bldg_occucy(String occucy){
        this.rbca_bldg_occucy = occucy;
    }
    
    public int getRBCA_bldg_occucy_avail() {
        return rbca_bldg_occucy_avail;
    }

    public void setRBCA_bldg_occucy_avail(int occucyAvailable) {
        this.rbca_bldg_occucy_avail = occucyAvailable;
    }
    
    public double getRBCA_bldg_stories(){
        return rbca_bldg_stories;
    }
    
    public void setRBCA_bldg_stories(double stories){
        this.rbca_bldg_stories = stories;
    }
    
    public double getRBCA_bldg_width(){
        return rbca_bldg_width;
    }
    
    public void setRBCA_bldg_width(double bldg_width){
        this.rbca_bldg_width = bldg_width;
    }
    
    public double getRBCA_bldg_length(){
        return rbca_bldg_length;
    }
    
    public void setRBCA_bldg_length(double bldg_length){
        this.rbca_bldg_length = bldg_length;
    }
    
    public String getRBCA_bldg_use(){
        return rbca_bldg_use;
    }
    public void setRBCA_bldg_use(String bldg_use){
        this.rbca_bldg_use = bldg_use;
    }
    
    public String getRBCA_bldg_use_oth(){
        return rbca_bldg_use_oth;
    }
    public void setRBCA_bldg_use_oth(String bldg_use_oth){
        this.rbca_bldg_use_oth = bldg_use_oth;
    }
    
    public int getRBCA_bldg_outbldg() {
        return rbca_bldg_outbldg;
    }
    public void setRBCA_bldg_outbldg(int outbldg) {
        this.rbca_bldg_outbldg = outbldg;
    }
    
    public String getRBCA_bldg_outbldg_notes(){
        return rbca_bldg_outbldg_notes;
    }
    public void setRBCA_bldg_outbldg_notes(String outbldg_notes){
        this.rbca_bldg_outbldg_notes = outbldg_notes;
    }
    
    public int getRBCA_bldg_units_res() {
        return rbca_bldg_units_res;
    }
    public void setRBCA_bldg_units_res(int units_res) {
        this.rbca_bldg_units_res = units_res;
    }
    
    public int getRBCA_bldg_units_comm() {
        return rbca_bldg_units_comm;
    }
    public void setRBCA_bldg_units_comm(int units_comm) {
        this.rbca_bldg_units_comm = units_comm;
    }
    
    public String getRBCA_bldg_occu_name(){
        return rbca_bldg_occu_name;
    }
    public void setRBCA_bldg_occu_name(String occu_name){
        this.rbca_bldg_occu_name = occu_name;
    }
    
    public int getRBCA_bldg_occu_phone() {
        return rbca_bldg_occu_phone;
    }
    public void setRBCA_bldg_occu_phone(int occu_phone) {
        this.rbca_bldg_occu_phone = occu_phone;
    }
    
    public String getRBCA_bldg_notes(){
        return rbca_bldg_notes;
    }
    public void setRBCA_bldg_notes(String bldg_notes){
        this.rbca_bldg_notes = bldg_notes;
    }
    
    public String getRBCA_hist_desig(){
        return rbca_hist_desig;
    }
    public void setRBCA_hist_desig(String hist_desig){
        this.rbca_hist_desig = hist_desig;
    }
    
    public String getRBCA_hist_desig_oth(){
        return rbca_hist_desig_oth;
    }
    public void setRBCA_hist_desig_oth(String hist_desig_oth){
        this.rbca_hist_desig_oth = hist_desig_oth;
    }
    
    public String getRBCA_hist_dist(){
        return rbca_hist_dist;
    }
    
    public void setRBCA_hist_dist(String hist_dist){
        this.rbca_hist_dist = hist_dist;
    }
    
    public String getRBCA_hist_dist_name(){
        return rbca_hist_dist_name;
    }
    
    public void setRBCA_hist_dist_name(String hist_dist_name){
        this.rbca_hist_dist_name = hist_dist_name;
    }
    
    public int getRBCA_hist_appear(){
        return rbca_hist_appear;
    }
    
    public void setRBCA_hist_appear(int hist_appear){
        this.rbca_hist_appear = hist_appear;
    }
    
    public int getRBCA_hist_age(){
        return rbca_hist_age;
    }
    
    public void setRBCA_hist_age(int hist_age){
        this.rbca_hist_age = hist_age;
    }
    
    public String getRBCA_hist_age_meta(){
        return rbca_hist_age_meta;
    }
    
    public void setRBCA_hist_age_meta(String hist_age_meta){
        this.rbca_hist_age_meta = hist_age_meta;
    }
    
    public int getRBCA_hist_yr_built(){
        return rbca_hist_yr_built;
    }
    
    public void setRBCA_hist_yr_built(int hist_yr_built){
        this.rbca_hist_yr_built = hist_yr_built;
    }
    
    
    public int getRBCA_hist_age_src(){
        return this.rbca_hist_age_src;
    }
    public void setRBCA_hist_age_src(int hist_age_src){
        this.rbca_hist_age_src = hist_age_src;
    }
    
    public String getRBCA_hist_age_src_oth(){
        return this.rbca_hist_age_src_oth;
    }
    public void setRBCA_hist_age_src_oth(String hist_age_src_oth){
        this.rbca_hist_age_src_oth = hist_age_src_oth;
    }
    
    public String getRBCA_hist_notes(){
        return this.rbca_hist_notes;
    }
    public void setRBCA_hist_notes(String hist_notes){
        this.rbca_hist_notes = hist_notes;
    }
    
    public String getRBCA_dmg_source(){
        return rbca_dmg_source;
    }
    public void setRBCA_dmg_source(String dmg_source){
        this.rbca_dmg_source = dmg_source;
    }
    
    public String getRBCA_dmg_source_oth(){
        return rbca_dmg_source_oth;
    }
    public void setRBCA_dmg_source_oth(String dmg_source_oth){
        this.rbca_dmg_source_oth = dmg_source_oth;
    }
    
    public int getRBCA_dmg_total(){
        return rbca_dmg_total;
    }
    
    public void setRBCA_dmg_total(int dmg_total){
        this.rbca_dmg_total = dmg_total;
    }
    
    public String getRBCA_dmg_desc(){
        return rbca_dmg_desc;
    }
    
    public void setRBCA_dmg_desc(String dmg_desc){
        this.rbca_dmg_desc = dmg_desc;
    }
    
    public String getRBCA_struct_type(){
        return rbca_struct_type;
    }
    public void setRBCA_struct_type(String struct_type){
        this.rbca_struct_type = struct_type;
    }
    
    public String getRBCA_struct_type_oth(){
        return rbca_struct_type_oth;
    }
    
    public void setRBCA_struct_type_oth(String struct_type_oth){
        this.rbca_struct_type_oth = struct_type_oth;
    }
    
    public void setRBCA_struct_defects(String struct_defects){
        this.rbca_struct_defects = struct_defects;
    }
    public String getRBCA_struct_defects(){
        return this.rbca_struct_defects;
    }
    
    public int getRBCA_struct(){
        return rbca_struct;
    }
    public void setRBCA_struct(int struct){
        this.rbca_struct = struct;
    }
    
    
    public String getRBCA_struct_notes(){
        return rbca_struct_notes;
    }
    public void setRBCA_struct_notes(String struct_notes){
        this.rbca_struct_notes = struct_notes;
    }
    
    public String getRBCA_found_type(){
        return rbca_found_type;
    }
    public void setRBCA_found_type(String found_type){
        this.rbca_found_type = found_type;
    }
    
    public String getRBCA_found_type_oth(){
        return rbca_found_type_oth;
    }
    public void setRBCA_found_type_oth(String found_type_oth){
        this.rbca_found_type_oth = found_type_oth;
    }
    
    public int getRBCA_found(){
        return rbca_found;
    }
    public void setRBCA_found(int found){
        this.rbca_found = found;
    }
    
    public String getRBCA_found_notes(){
        return rbca_found_notes;
    }
    public void setRBCA_found_notes(String found_notes){
        this.rbca_found_notes = found_notes;
    }
    
    public String getRBCA_extwall_mat(){
        return rbca_extwall_mat;
    }
    public void setRBCA_extwall_mat(String extwall_mat){
        this.rbca_extwall_mat = extwall_mat;
    }
    
    public String getRBCA_extwall_mat_oth(){
        return rbca_extwall_mat_oth;
    }
    public void setRBCA_extwall_mat_oth(String extwall_mat_oth){
        this.rbca_extwall_mat_oth = extwall_mat_oth;
    }
    
    public int getRBCA_extwall(){
        return rbca_extwall;
    }
    public void setRBCA_extwall(int extwall){
        this.rbca_extwall = extwall;
    }
    
    public String getRBCA_extwall_notes(){
        return rbca_extwall_notes;
    }
    public void setRBCA_extwall_notes(String extwall_notes){
        this.rbca_extwall_notes = extwall_notes;
    }
    
    public String getRBCA_extfeat_type(){
        return rbca_extfeat_type;
    }
    public void setRBCA_extfeat_type(String extfeat_type){
        this.rbca_extfeat_type = extfeat_type;
    }
    
    public String getRBCA_extfeat_type_oth(){
        return rbca_extfeat_type_oth;
    }
    public void setRBCA_extfeat_type_oth(String extfeat_type_oth){
        this.rbca_extfeat_type_oth = extfeat_type_oth;
    }
    
    public int getRBCA_extfeat(){
        return rbca_extfeat;
    }
    public void setRBCA_extfeat(int extfeat){
        this.rbca_extfeat = extfeat;
    }
    
    public String getRBCA_extfeat_notes(){
        return rbca_extfeat_notes;
    }
    public void setRBCA_extfeat_notes(String extfeat_notes){
        this.rbca_extfeat_notes = extfeat_notes;
    }
    
    public String getRBCA_win_type(){
        return rbca_win_type;
    }
    public void setRBCA_win_type(String win_type){
        this.rbca_win_type = win_type;
    }
    
    public String getRBCA_win_type_oth(){
        return rbca_win_type_oth;
    }
    public void setRBCA_win_type_oth(String win_type_oth){
        this.rbca_win_type_oth = win_type_oth;
    }
    
    public String getRBCA_win_mat(){
        return rbca_win_mat;
    }
    public void setRBCA_win_mat(String win_mat){
        this.rbca_win_mat = win_mat;
    }
    
    public String getRBCA_win_mat_oth(){
        return rbca_win_mat_oth;
    }
    public void setRBCA_win_mat_oth(String win_mat_oth){
        this.rbca_win_mat_oth = win_mat_oth;
    }
    
    public int getRBCA_win(){
        return rbca_win;
    }
    public void setRBCA_win(int win){
        this.rbca_win = win;
    }
    
    public String getRBCA_win_notes(){
        return rbca_win_notes;
    }
    public void setRBCA_win_notes(String win_notes){
        this.rbca_win_notes = win_notes;
    }
    
    public String getRBCA_roof_type(){
        return rbca_roof_type;
    }
    public void setRBCA_roof_type(String roof_type){
        this.rbca_roof_type = roof_type;
    }
    
    public String getRBCA_roof_type_oth(){
        return rbca_roof_type_oth;
    }
    public void setRBCA_roof_type_oth(String roof_type_oth){
        this.rbca_roof_type_oth = roof_type_oth;
    }
    
    public String getRBCA_roof_mat(){
        return rbca_roof_mat;
    }
    public void setRBCA_roof_mat(String roof_mat){
        this.rbca_roof_mat = roof_mat;
    }
    
    public String getRBCA_roof_mat_oth(){
        return rbca_roof_mat_oth;
    }
    public void setRBCA_roof_mat_oth(String roof_mat_oth){
        this.rbca_roof_mat_oth = roof_mat_oth;
    }
    
    public int getRBCA_roof(){
        return rbca_roof;
    }
    public void setRBCA_roof(int roof){
        this.rbca_roof = roof;
    }
    
    public String getRBCA_roof_notes(){
        return rbca_roof_notes;
    }
    public void setRBCA_roof_notes(String roof_notes){
        this.rbca_roof_notes = roof_notes;
    }
    
    public String getRBCA_int_cond(){
        return rbca_int_cond;
    }
    public void setRBCA_int_cond(String int_cond){
        this.rbca_int_cond = int_cond;
    }
    
    public int getRBCA_int_collect_extant(){
        return rbca_int_collect_extant;
    }
    public void setRBCA_int_collect_extant(int int_collect_extant){
        this.rbca_int_collect_extant = int_collect_extant;
    }
    
    public String getRBCA_int_collect_type(){
        return rbca_int_collect_type;
    }
    public void setRBCA_int_collect_type(String int_collect_type){
        this.rbca_int_collect_type = int_collect_type;
    }
    
    public String getRBCA_int_collect_type_oth(){
        return rbca_int_collect_type_oth;
    }
    public void setRBCA_int_collect_type_oth(String int_collect_type_oth){
        this.rbca_int_collect_type_oth = int_collect_type_oth;
    }
    
    public String getRBCA_int_img1(){
        return rbca_int_img1;
    }
    
    public void setRBCA_int_img1(String int_img1){
        this.rbca_int_img1 = int_img1;
    }
    
    public String getRBCA_int_desc1(){
        return rbca_int_desc1;
    }
    
    public void setRBCA_int_desc1(String int_desc1){
        this.rbca_int_desc1 = int_desc1;
    }
    
    public String getRBCA_int_img2(){
        return rbca_int_img2;
    }
    
    public void setRBCA_int_img2(String int_img2){
        this.rbca_int_img2 = int_img2;
    }
    
    public String getRBCA_int_desc2(){
        return rbca_int_desc2;
    }
    
    public void setRBCA_int_desc2(String int_desc2){
        this.rbca_int_desc2 = int_desc2;
    }
    
    public String getRBCA_int_img3(){
        return rbca_int_img3;
    }
    
    public void setRBCA_int_img3(String int_img3){
        this.rbca_int_img3 = int_img3;
    }
    
    public String getRBCA_int_desc3(){
        return rbca_int_desc3;
    }
    
    public void setRBCA_int_desc3(String int_desc3){
        this.rbca_int_desc3 = int_desc3;
    }
    
    public String getRBCA_int_notes(){
        return rbca_int_notes;
    }
    public void setRBCA_int_notes(String int_notes){
        this.rbca_int_notes = int_notes;
    }
    
    public String getRBCA_landveg_feat(){
        return rbca_landveg_feat;
    }
    public void setRBCA_landveg_feat(String landveg_feat){
        this.rbca_landveg_feat = landveg_feat;
    }
    
    public String getRBCA_landveg_feat_oth(){
        return rbca_landveg_feat_oth;
    }
    public void setRBCA_landveg_feat_oth(String landveg_feat_oth){
        this.rbca_landveg_feat_oth = landveg_feat_oth;
    }
    
    public int getRBCA_landveg(){
        return rbca_landveg;
    }
    public void setRBCA_landveg(int landveg){
        this.rbca_landveg = landveg;
    }
    
    public String getRBCA_landveg_notes(){
        return rbca_landveg_notes;
    }
    public void setRBCA_landveg_notes(String landveg_notes){
        this.rbca_landveg_notes = landveg_notes;
    }
    
    public String getRBCA_landblt_feat(){
        return rbca_landblt_feat;
    }
    public void setRBCA_landblt_feat(String landblt_feat){
        this.rbca_landblt_feat = landblt_feat;
    }
    public String getRBCA_landblt_feat_oth(){
        return rbca_landblt_feat_oth;
    }
    public void setRBCA_landblt_feat_oth(String landblt_feat_oth){
        this.rbca_landblt_feat_oth = landblt_feat_oth;
    }
    
    public int getRBCA_landblt(){
        return rbca_landblt;
    }
    public void setRBCA_landblt(int landblt){
        this.rbca_landblt = landblt;
    }
    public String getRBCA_landblt_notes(){
        return rbca_landblt_notes;
    }
    public void setRBCA_landblt_notes(String landblt_notes){
        this.rbca_landblt_notes = landblt_notes;
    }
    
    public String getRBCA_media_img1(){
        return rbca_media_img1;
    }
    
    public void setRBCA_media_img1(String media_img1){
        this.rbca_media_img1 = media_img1;
    }
    
    public String getRBCA_media_desc1(){
        return rbca_media_desc1;
    }
    
    public void setRBCA_media_desc1(String media_desc1){
        this.rbca_media_desc1 = media_desc1;
    }
    
    public String getRBCA_media_img2(){
        return rbca_media_img2;
    }
    
    public void setRBCA_media_img2(String media_img2){
        this.rbca_media_img2 = media_img2;
    }
    
    public String getRBCA_media_desc2(){
        return rbca_media_desc2;
    }
    
    public void setRBCA_media_desc2(String media_desc2){
        this.rbca_media_desc2 = media_desc2;
    }
    
    public String getRBCA_media_img3(){
        return rbca_media_img3;
    }
    
    public void setRBCA_media_img3(String media_img3){
        this.rbca_media_img3 = media_img3;
    }
    
    public String getRBCA_media_desc3(){
        return rbca_media_desc3;
    }
    
    public void setRBCA_media_desc3(String media_desc3){
        this.rbca_media_desc3 = media_desc3;
    }
    
    public String getRBCA_media_img4(){
        return rbca_media_img4;
    }
    
    public void setRBCA_media_img4(String media_img4){
        this.rbca_media_img4 = media_img4;
    }
    
    public String getRBCA_media_desc4(){
        return rbca_media_desc4;
    }
    
    public void setRBCA_media_desc4(String media_desc4){
        this.rbca_media_desc4 = media_desc4;
    }
    
    public String getRBCA_media_img5(){
        return rbca_media_img5;
    }
    
    public void setRBCA_media_img5(String media_img5){
        this.rbca_media_img5 = media_img5;
    }
    
    public String getRBCA_media_desc5(){
        return rbca_media_desc5;
    }
    
    public void setRBCA_media_desc5(String media_desc5){
        this.rbca_media_desc5 = media_desc5;
    }
    
    public String getRBCA_media_img6(){
        return rbca_media_img6;
    }
    
    public void setRBCA_media_img6(String media_img6){
        this.rbca_media_img6 = media_img6;
    }
    
    public String getRBCA_media_desc6(){
        return rbca_media_desc6;
    }
    
    public void setRBCA_media_desc6(String media_desc6){
        this.rbca_media_desc6 = media_desc6;
    }
    
    public int getRBCA_hzrd(){
        return this.rbca_hzrd;
    }
    public void setRBCA_hzrd(int hzrd){
        this.rbca_hzrd = hzrd;
    }
    
    public String getRBCA_hzrd_type(){
        return this.rbca_hzrd_type;
    }
    public void setRBCA_hzrd_type(String hzrd_type){
        this.rbca_hzrd_type = hzrd_type;
    }
    
    public String getRBCA_hzrd_type_oth(){
        return this.rbca_hzrd_type_oth;
    }
    public void setRBCA_hzrd_type_oth(String hzrd_type_oth){
        this.rbca_hzrd_type_oth = hzrd_type_oth;
    }
    
    public String getRBCA_hzrd_notes(){
        return this.rbca_hzrd_notes;
    }
    public void setRBCA_hzrd_notes(String hzrd_notes){
        this.rbca_hzrd_notes = hzrd_notes;
    }
    
    public String getRBCA_hzrd_hazmat(){
        return this.rbca_hzrd_hazmat;
    }
    public void setRBCA_hzrd_hazmat(String hzrd_hazmat){
        this.rbca_hzrd_hazmat = hzrd_hazmat;
    }
    
    public String getRBCA_hzrd_hazmat_oth(){
        return this.rbca_hzrd_hazmat_oth;
    }
    public void setRBCA_hzrd_hazmat_oth(String hzrd_hazmat_oth){
        this.rbca_hzrd_hazmat_oth = hzrd_hazmat_oth;
    }
    
    public String getRBCA_actn(){
        return this.rbca_actn;
    }
    public void setRBCA_actn(String actn){
        this.rbca_actn = actn;
    }
    
    public String getRBCA_actn_oth(){
        return this.rbca_actn_oth;
    }
    public void setRBCA_actn_oth(String actn_oth){
        this.rbca_actn_oth = actn_oth;
    }
    
    public String getRBCA_eval(){
        return this.rbca_eval;
    }
    public void setRBCA_eval(String eval){
        this.rbca_eval = eval;
    }
    
    public String getRBCA_eval_oth(){
        return this.rbca_eval_oth;
    }
    public void setRBCA_eval_oth(String eval_oth){
        this.rbca_eval_oth = eval_oth;
    }
   
    
    //end of modification MoCA for Android
}
