package br.senai.sp.informatica.mobile.bikemobi.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class JSONParser {
    private static HttpURLConnection configConnection(String url)
            throws MalformedURLException, IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-Charset", "utf-8");
        con.setRequestProperty("Authorization"
                , "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6WyI4IiwiOCJdLCJqdGkiOiJhNzk0MDc5NmIxZWQ0ZjU1YjUxYjc5NjAwZDJkNzViZiIsIk5vbWUiOiJyYW5kcmFkZTMiLCJlbWFpbCI6InJvZG9sZm8uYW5kcmFkZUBmYXRlYy5zcC5nb3YuYnIiLCJuYmYiOjE1MjI1MDkwNzcsImV4cCI6MTUyMjUxMjY3NywiaWF0IjoxNTIyNTA5MDc3LCJpc3MiOiJCaWtlTW9iaUlzc3VlciIsImF1ZCI6IkJpa2VNb2JpQXVkaWVuY2UifQ.DxvVuhgpufDYFN906a-3e9W-Elek-x2avUDtTyiWt6rYWoopOtGrDdND2qpK-YTMd-x3SFfgTRjkDU1udHJ_LceTzFRHiLq64awD6A6YNQh88kGBT5JtVzwxA06Qd43DCuILt5gLf_N4Ht5WPBKbYsu023nHKwCauLHilHTiUAN2_YF88osYaGoz01xwvpQLzcsp7v_Rkf4CX33j8xV0oezZV0XY9n-P6hSGTFUfdBJSZS7ZCM_hK0Vs_yPod879egy5OstQNLHG8KN6DjikFeKgq4ZgQwgkLc-azafPTORIsr-L-e-XrfY_pJGRzwt1vAkQcu5NgeO_GBTjycvpbw");
        con.setConnectTimeout(15000);
        con.setReadTimeout(10000);

        return con;
    }

    private static HttpURLConnection configConnection(String url, String token)
            throws MalformedURLException, IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-Charset", "utf-8");
        con.setRequestProperty("Authorization"
                , "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6WyI4IiwiOCJdLCJqdGkiOiJhNzk0MDc5NmIxZWQ0ZjU1YjUxYjc5NjAwZDJkNzViZiIsIk5vbWUiOiJyYW5kcmFkZTMiLCJlbWFpbCI6InJvZG9sZm8uYW5kcmFkZUBmYXRlYy5zcC5nb3YuYnIiLCJuYmYiOjE1MjI1MDkwNzcsImV4cCI6MTUyMjUxMjY3NywiaWF0IjoxNTIyNTA5MDc3LCJpc3MiOiJCaWtlTW9iaUlzc3VlciIsImF1ZCI6IkJpa2VNb2JpQXVkaWVuY2UifQ.DxvVuhgpufDYFN906a-3e9W-Elek-x2avUDtTyiWt6rYWoopOtGrDdND2qpK-YTMd-x3SFfgTRjkDU1udHJ_LceTzFRHiLq64awD6A6YNQh88kGBT5JtVzwxA06Qd43DCuILt5gLf_N4Ht5WPBKbYsu023nHKwCauLHilHTiUAN2_YF88osYaGoz01xwvpQLzcsp7v_Rkf4CX33j8xV0oezZV0XY9n-P6hSGTFUfdBJSZS7ZCM_hK0Vs_yPod879egy5OstQNLHG8KN6DjikFeKgq4ZgQwgkLc-azafPTORIsr-L-e-XrfY_pJGRzwt1vAkQcu5NgeO_GBTjycvpbw");
        con.setConnectTimeout(15000);
        con.setReadTimeout(10000);

        return con;
    }

    private static String readJson(BufferedReader in) throws IOException {
        StringBuilder buffer = new StringBuilder(100);
        String linha = in.readLine();
        while (linha != null) {
            buffer.append(linha);
            linha = in.readLine();
        }
        return buffer.toString();
    }

    public interface ResponseCodeCallBack {
        public abstract void setResponse(int code);
    }

    public static class Remover extends AsyncTask<Void, Void, Integer> {
        private ResponseCodeCallBack callBack;
        private HttpURLConnection con;
        private String url;

        public Remover(String url ,ResponseCodeCallBack callBack) {
            this.callBack = callBack;
            this.url = url;
        }

        public Remover(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            try {
                con = configConnection(url);
            } catch (Exception ex) {
                Log.e("Remover.pre", ex.getMessage());
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int code = HttpURLConnection.HTTP_INTERNAL_ERROR;
            try {
                if (con != null) {
                    con.setRequestMethod("DELETE");
                    con.connect();
                    code = con.getResponseCode();
                    con.disconnect();
                }
            } catch (IOException ex) {
                Log.e("Remover.pre", ex.getMessage());
            }

            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            if(callBack != null)
                callBack.setResponse(code);
        }
    }

    public interface LocationAndDataCallBack {
        public abstract void setResponse(int code, String location, String json);
    }

    public static class Incluir extends AsyncTask<Void, Void, String> {
        private LocationAndDataCallBack callBack;
        private HttpURLConnection con;
        private String outputData;
        private String location;
        private int returnCode;
        private String url;

        public Incluir(String url, String data,LocationAndDataCallBack callBack) {
            this.callBack = callBack;
            this.outputData = data;
            this.url = url;
        }

        public Incluir(String url, String data) {
            this.outputData = data;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            try {
                con = configConnection(url);
            } catch (Exception ex) {
                Log.e("Incluir.pre", ex.getMessage());
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            returnCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            String data = null;

            try {
                if (con != null) {
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                    out.write(outputData);
                    out.flush();
                    out.close();

                    returnCode = con.getResponseCode();

                    //Log.d("BikeLog", "returnCode: " + returnCode);
                    //Log.d("BikeLog", "HttpURLConnection.HTTP_CREATED: " + HttpURLConnection.HTTP_CREATED);
                    if(returnCode == HttpURLConnection.HTTP_CREATED) {
                        location = con.getHeaderField("location");
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        data = readJson(in);
                        in.close();
                    }
                    if (returnCode == HttpURLConnection.HTTP_OK){
                        location = con.getHeaderField("location");
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        data = readJson(in);
                        in.close();
                    }

                    con.disconnect();
                }
            } catch (IOException ex) {
                Log.e("Incluir.pre", ex.getMessage());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            if(callBack != null) {
                callBack.setResponse(returnCode, location, data);
            }
            //Log.d("BikeLog", "data: " + data);

        }
    }

    public interface LocationCallBack {
        public abstract void setResponse(int code, String location);
    }

    public static class Alterar extends AsyncTask<Void, Void, Integer> {
        private LocationCallBack callBack;
        private HttpURLConnection con;
        private String outputData;
        private String location;
        private String url;

        public Alterar(String url, String data,LocationCallBack callBack) {
            this.callBack = callBack;
            this.outputData = data;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            try {
                con = configConnection(url);
            } catch (Exception ex) {
                Log.e("Alterar.pre", ex.getMessage());
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int code = HttpURLConnection.HTTP_INTERNAL_ERROR;
            String data = null;

            try {
                if (con != null) {
                    con.setRequestMethod("PUT");
                    con.setDoOutput(true);
                    con.connect();

                    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                    out.write(outputData);
                    out.flush();
                    out.close();

                    code = con.getResponseCode();
                    if(code == HttpURLConnection.HTTP_OK) {
                        location = con.getHeaderField("location");
                    }
                    con.disconnect();
                }
            } catch (IOException ex) {
                Log.e("Alterar.in", ex.getMessage());
            }

            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            if(callBack != null) {
                callBack.setResponse(code, location);
            }
        }
    }

    public interface DataCallBack {
        public abstract void setResponse(int code, String json);
    }

    public static class Consultar extends AsyncTask<Void, Void, String> {
        private DataCallBack callBack;
        private HttpURLConnection con;
        private int returnCode;
        private String url;

        public Consultar(String url, DataCallBack callBack) {
            this.callBack = callBack;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            try {
                con = configConnection(url);
            } catch (Exception ex) {
                Log.e("Consultar.pre", ex.getMessage());
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            returnCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            String data = null;

            try {
                if (con != null) {
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.connect();

                    returnCode = con.getResponseCode();
                    if(returnCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        data = readJson(in);
                        in.close();
                    }
                    con.disconnect();
                }
            } catch (IOException ex) {
                Log.e("Consultar.in", ex.getMessage());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            if(callBack != null) {
                callBack.setResponse(returnCode, data);
            }
        }
    }

}
