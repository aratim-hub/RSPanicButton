package com.theappwelt.vhelp.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import com.theappwelt.vhelp.model.Constants;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by applink on 4/1/19.
 */

public class Utils {

    Context context;

    public static void init(Context c) {

        // Initializing SPHandler.
        //SPHandler.setApplicationContext(c);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    // show dialog
    public static void showDialog(final Context c, final String message,
                                  final boolean closeActivity, final boolean reRefresh) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(message).setTitle(Constants.APP_NAME);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if (closeActivity) {
                    ((Activity) c).finish();
                }
                if (reRefresh) {
                    if (Build.VERSION.SDK_INT >= 11) {
                        //((Activity) c).finish();
                        ((Activity) c).recreate();
                    } else {
                        ((Activity) c).startActivity(((Activity) c).getIntent());
                        ((Activity) c).finish();
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    public static boolean removeDirectory(File directory) {

        // System.out.println("removeDirectory " + directory);

        if (directory == null)
            return false;
        if (!directory.exists())
            return true;
        if (!directory.isDirectory())
            return false;

        String[] list = directory.list();

        // Some JVMs return null for File.list() when the
        // directory is empty.
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File entry = new File(directory, list[i]);

                //        System.out.println("\tremoving entry " + entry);

                if (entry.isDirectory()) {
                    if (!removeDirectory(entry))
                        return false;
                } else {
                    if (!entry.delete())
                        return false;
                }
            }
        }

        return directory.delete();
    }


    /* // show dialog
     public static void showDialogForWrongMobileNoEntered(final Context c, final String message,
                                                    final boolean closeActivity, final boolean reRefresh) {
         AlertDialog.Builder builder = new AlertDialog.Builder(c);
         builder.setMessage(message).setTitle(null);
         builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
                 if(closeActivity){
                     ((Activity) c).finish();
                 }if(reRefresh){
                     if (Build.VERSION.SDK_INT >= 11)
                     {
                         //((Activity) c).finish();
                         ((Activity) c).recreate();
                     }
                     else
                     {
                         ((Activity) c).startActivity(((Activity) c).getIntent());
                         ((Activity) c).finish();
                     }
                 }
             }
         });
         builder.setPositiveButton("SIGN UP", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
                 dialog.dismiss();
                 Intent intent=new Intent(c,SignUpActivity.class);
                 c.startActivity(intent);
             }
         });

         AlertDialog dialog = builder.create();
         dialog.show();
     }
 */
  /*  // show dialog
    public static void showDialogForSessionTimeout(final Context c, final String message,
                                  final boolean closeActivity, final boolean reRefresh) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(message).setTitle(null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent=new Intent(c, HomeActivity.class);
                c.startActivity(intent);
                ((Activity) c).finish();


                if(closeActivity){
                    ((Activity) c).finish();
                }if(reRefresh){
                    if (Build.VERSION.SDK_INT >= 11)
                    {
                        //((Activity) c).finish();
                        ((Activity) c).recreate();
                    }
                    else
                    {
                        ((Activity) c).startActivity(((Activity) c).getIntent());
                        ((Activity) c).finish();
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
*/
    public static boolean isOkToSave(String data) {
        if (data != null && !data.equals("") && !data.equalsIgnoreCase("Not Specified") && !data.equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }


    /*private static ProgressDialog progressDialog;

	public static void startProgressDialog(Context context,
			String progressMessage) {
		progressDialog = new ProgressDialog(context, android.R.style.Theme_Translucent);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		progressDialog.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		progressDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(progressMessage);
		progressDialog.setCancelable(false);
		progressDialog.show();
		//progressDialog.setContentView(R.layout.progress_bar);
		//TextView tv = (TextView) progressDialog.findViewById(R.id.progress_text_tv);
		//tv.setText(progressMessage);
	}

    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
*/
    //To check for internet connection
    public static boolean isConnectedToInternet(Context c) {
        ConnectivityManager connectivity = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
                if (info.isConnectedOrConnecting())
                    return true;

        }
        return false;
    }

    //hide keyboard
    public static void hideKeyboard(Context c) {
        // ((Activity) c).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        InputMethodManager imm = (InputMethodManager) ((Activity) c).getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = ((Activity) c).getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(((Activity) c));
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context c) {

        InputMethodManager imm = (InputMethodManager)
                (InputMethodManager) ((Activity) c).getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((Activity) c).getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(((Activity) c));
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

    }

    public static String calculateTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        return minute + ":" + second;

        //System.out.println("Day " + day + " Hour " + hours + " Minute " + minute + " Seconds " + second);

    }


}
