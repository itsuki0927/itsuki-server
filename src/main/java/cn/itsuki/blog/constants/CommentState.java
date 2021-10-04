package cn.itsuki.blog.constants;

/**
 * 评论状态
 *
 * @author: itsuki
 * @create: 2021-10-04 12:40
 **/
public class CommentState {
    // 待审核
    public static final int Auditing = 0;
    // 通过正常
    public static final int Published = 1;
    // 已删除
    public static final int Deleted = -1;
    // 垃圾评论
    public static final int Spam = -2;

}
