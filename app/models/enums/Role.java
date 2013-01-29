package models.enums;

/**
 * @author wujinliang
 * @since 2011-10-11
 */
public enum Role {
    /**
     * 普通用户
     */
    USER(0),

    /**
     * 测试人员
     */
    Tester(1),

    /**
     * 产品经理
     */
    PDM(1),

    /**
     * 运营人员
     */
    Operator(40),

    /**
     * 推广人员
     */
    Market(1),

    /**
     * 后台编辑人员
     */
    Editor(50),

    /**
     * 管理员
     */
    ADMIN(100);

    /**
     * Role level.
     */
    public final int level;

    /**
     * Construct a role.
     * @param level Role level
     */
    private Role(int level) {
        this.level = level;
    }

    /**
     * Create role from text.
     * @param text Text which should match the role name, ignoring case
     * @return Role if text match any, <code>null</code> otherwise
     */
    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.toString().equalsIgnoreCase(text)) {
                return role;
            }
        }
        return null;
    }
}
