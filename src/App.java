import business.UserManager;
import core.Db;

import core.Helper;
import view.AdminView;
import view.LoginView;


public class App {
    public static void main(String[] args) {
        Helper.setTheme();
        //LoginView loginView = new LoginView();
        UserManager userManager = new UserManager(); // herseferinde kullanıcı adı ve şifre girişi yapmamak için bilgileri giriyoruz
        AdminView adminView = new AdminView(userManager.findByLogin("admin", "1234"));

    }
}