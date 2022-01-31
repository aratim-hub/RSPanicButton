package com.theappwelt.vhelp.utilities;


import android.content.Context;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;


public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    private Context context;


    public ServiceHandler(Context context) {
        this.context = context;
    }

    /*
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method) {

        return this.makeServiceCall(url, method, null);
    }

//    public String makeServiceCallwithAuth(String url, int method, RequestBody formBody, String Credientials) {
//
//        return this.makeServiceCallwithAuth(url, method, formBody, Credientials);
//    }

    public String makeServiceCall(String url, int method, RequestBody formBody) {
        if (method == POST) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .authenticator(new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            if (response.request().header("Authorization") != null) {
                                return null; // Give up, we've already attempted to authenticate.
                            }
                            String credential = Credentials.basic("admin", "Admin@123");
                            return response.request().newBuilder().header("Authorization", credential).build();
                        }
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

//            MediaType type = MediaType.parse("application/x-www-form-urlencoded");

            String boundary = "" + System.currentTimeMillis() + "";
            Request request = new Request.Builder()
                    .url(url)
//                    .post(formBody)
                    .method("POST", formBody)
                    .addHeader("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .build();

            try {
                Response responseR = client.newCall(request).execute();

                response = responseR.body().string();
                // Do something with the response.
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        } else if (method == GET) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .authenticator(new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            if (response.request().header("Authorization") != null) {
                                return null; // Give up, we've already attempted to authenticate.
                            }

                            String credential = Credentials.basic("admin", "Admin@");
                            return response.request().newBuilder().header("Authorization", credential).build();
                        }
                    })

                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", formBody)
                    .build();
            try {
                Response responseR = client.newCall(request).execute();

                response = responseR.body().string();
                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        return response;

    }

    public String makeServiceCallWithAuth(String url, int method, RequestBody formBody, String Credientials) {
        if (method == POST) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .authenticator(new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            if (response.request().header("Authorization") != null) {
                                return null; // Give up, we've already attempted to authenticate.
                            }
                            return response.request().newBuilder().header("Authorization", Credientials).build();
                        }
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

//            MediaType type = MediaType.parse("application/x-www-form-urlencoded");

            String boundary = "" + System.currentTimeMillis() + "";
            Request request = new Request.Builder()
                    .url(url)
//                    .post(formBody)
                    .method("POST", formBody)
                    .addHeader("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .build();

            try {
                Response responseR = client.newCall(request).execute();

                response = responseR.body().string();
                // Do something with the response.
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        } else if (method == GET) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .authenticator(new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            if (response.request().header("Authorization") != null) {
                                return null; // Give up, we've already attempted to authenticate.
                            }


                            return response.request().newBuilder().header("Authorization", Credientials).build();
                        }
                    })

                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", formBody)
                    .build();
            try {
                Response responseR = client.newCall(request).execute();

                response = responseR.body().string();
                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        return response;

    }
}
