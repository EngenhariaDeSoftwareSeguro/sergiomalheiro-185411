import 'package:shared_preferences/shared_preferences.dart';

class TokenWrapper {
  /*static String getToken() {
    SharedPreferences.getInstance().then((SharedPreferences prefs) {
      return prefs.getString('token') ?? '';
    });
  }*/

  static Future<String> getUserAsync() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String result = prefs.getString('user')?? '';
    return result;
  }

  static Future<String> getTokenAsync() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String result = prefs.getString('token')?? '';
    return result;
  }

  static void setToken(String user, String token) {
    SharedPreferences.getInstance().then((SharedPreferences prefs) {
      prefs.setString('user', user);
      prefs.setString('token', token);
    });
  }

  static void clear() {
    SharedPreferences.getInstance().then((SharedPreferences prefs) {
      prefs.remove('user');
      prefs.remove('token');
    });
  }
}
