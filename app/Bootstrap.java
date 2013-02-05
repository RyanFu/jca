import models.Setting;
import models.User;
import models.enums.Role;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.Codec;

/**
 * Application Startup
 * @author wujinliang
 * @since 2012/09/24
 */
@OnApplicationStart
public class Bootstrap extends Job {
    @Override
    public void doJob() {
        User linjb = User.find("byName", "linjb").first();
        if (linjb == null) {
            linjb = new User();
            linjb.name = "linjb";
            linjb.password = Codec.hexMD5("ljb123");
            linjb.role = Role.Operator;
            linjb.save();
        }

        User admin = User.find("byName", "admin").first();
        if (admin == null) {
            admin = new User();
            admin.name = "admin";
            admin.password = Codec.hexMD5("kanios@2013");
            admin.role = Role.ADMIN;
            admin.save();
        }

        Setting feedback = Setting.find("byType", "feedback").first();
        if (feedback == null) {
            feedback = new Setting();
            feedback.title = "用户反馈";
            feedback.type = "feedback";
            feedback.save();
        }
    }
}
