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
    
    //Fields added Jorge Rodriguez
    private String rbca_coord_loc,rbca_bldg_use,rbca_bldg_use_oth, rbca_bldg_outbldg_notes,rbca_bldg_occu_name,rbca_bldg_notes;
    private double RBCA_altitude, RBCA_accuracy, rbca_bldg_stories, rbca_bldg_width, rbca_bldg_length;
    private String rbca_coord_notes, rbca_addr_no, rbca_addr_street, rbca_bldg_area, rbca_bldg_posting;
    private String rbca_bldg_posting_oth, rbca_coord_loc_oth,rbca_coord_corner,rbca_bldg_occucy, rbca_hist_desig, rbca_hist_desig_oth;
    private int rbca_bldg_occucy_avail, rbca_bldg_outbldg,rbca_bldg_units_res, rbca_bldg_units_comm, rbca_bldg_occu_phone;
    
    //end of Field added
    

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
            this.rbca_coord_loc = postVals.get(29).toString();
            this.rbca_coord_loc_oth = postVals.get(30).toString();
            this.rbca_coord_corner = postVals.get(31).toString();
            this.rbca_coord_notes = postVals.get(32).toString();
            this.rbca_addr_no = postVals.get(33).toString();
            this.rbca_addr_street = postVals.get(34).toString();
            this.rbca_bldg_area = postVals.get(35).toString();
            this.rbca_bldg_posting = postVals.get(36).toString();
            this.rbca_bldg_posting_oth = postVals.get(37).toString();
            this.rbca_bldg_occucy = postVals.get(38).toString();
            this.rbca_bldg_occucy_avail = (Integer) postVals.get(39);
            this.rbca_bldg_stories = (Double) postVals.get(40);
            this.rbca_bldg_width = (Double) postVals.get(41);
            this.rbca_bldg_length = (Double) postVals.get(42);
            this.rbca_bldg_use = postVals.get(43).toString();
            this.rbca_bldg_use_oth = postVals.get(44).toString();
            this.rbca_bldg_outbldg = (Integer) postVals.get(45);
            this.rbca_bldg_outbldg_notes = postVals.get(46).toString();
            this.rbca_bldg_units_res = (Integer) postVals.get(47);
            this.rbca_bldg_units_comm = (Integer) postVals.get(48);
            this.rbca_bldg_occu_name = postVals.get(49).toString();
            this.rbca_bldg_occu_phone = (Integer) postVals.get(50);
            this.rbca_bldg_notes = postVals.get(51).toString();
            this.rbca_hist_desig = postVals.get(52).toString();
            this.rbca_hist_desig_oth = postVals.get(53).toString();
            //
            this.isLocalChange = (Integer) postVals.get(54) > 0;
            
            
        } else {
            this.id = -1;
        }
    }

    public Post(int blog_id, String title, String content, String picturePaths, long date, String categories, String tags, String status,
            String password, double latitude, double longitude, boolean isPage, String postFormat, boolean createBlogReference, boolean isLocalChange, 
            String rbca_coord_loc,String rbca_coord_loc_oth, String rbca_coord_corner, String rbca_coord_notes, String rbca_addr_no, String rbca_addr_street,
            String rbca_bldg_area, String rbca_bldg_posting, String rbca_bldg_posting_oth, String rbca_bldg_occucy, int rbca_bldg_occucy_avail,
            double rbca_bldg_stories,double bldg_width, double bldg_length, String bldg_use, String bldg_use_oth, int rbca_bldg_outbldg, String outbldg_notes, 
            int units_res, int units_comm, String occu_name, int occu_phone, String occu_notes, String hist_desig, String hist_desig_oth) {
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
        
        //added Jorge 
        this.rbca_coord_loc = rbca_coord_loc;
        this.rbca_coord_loc_oth = rbca_coord_loc_oth;
        this.rbca_coord_corner = rbca_coord_corner;
        this.rbca_coord_notes = rbca_coord_notes;
        this.rbca_addr_no = rbca_addr_no;
        this.rbca_addr_street = rbca_addr_street;
        this.rbca_bldg_area = rbca_bldg_area;
        this.rbca_bldg_posting = rbca_bldg_posting;
        this.rbca_bldg_posting_oth = rbca_bldg_posting_oth;
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
    
    
    
    ///////////////////////surveys for android
    
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
    
    //end of modification to SiteCondition
}
