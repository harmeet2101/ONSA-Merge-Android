package co.uk.depotnet.onsa.networking;

import android.content.Context;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import co.uk.depotnet.onsa.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

    private static Retrofit retrofit = null;
    private static OkHttpClient.Builder httpClientBuilder;

    private static Context context;

    public void initContext(Context context){
        APIClient.context = context;
    }
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());



    static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .followSslRedirects(true)
                .followRedirects(true)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor);

//        OkHttpClient okHttpClient = enableTls12OnPreLollipop(okHttpClientBuilder).build();
        OkHttpClient okHttpClient = okHttpClientBuilder.build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

//    private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
//        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
//            try {
//                SSLContext sc = SSLContext.getInstance("TLSv1.2");
//                sc.init(null, null, null);
//                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));
//
//                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                        .tlsVersions(TlsVersion.TLS_1_2).build();
//
//                List<ConnectionSpec> specs = new ArrayList<>();
//                specs.add(cs);
//                specs.add(ConnectionSpec.COMPATIBLE_TLS);
//                specs.add(ConnectionSpec.CLEARTEXT);
//
//                client.connectionSpecs(specs);
//            } catch (Exception exc) {
//                exc.printStackTrace();
//            }
//        }
//
//        return client;
//    }


    static <S> S createService( Class<S> serviceClass, String userToken) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder = new OkHttpClient.Builder().connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .writeTimeout(90, TimeUnit.SECONDS);

        httpClientBuilder.addInterceptor(interceptor);



        final String basic =
                "Bearer " + userToken;

        httpClientBuilder.addInterceptor(chain -> {

            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
                        .header("Accept", "application/json")

                        .method(original.method(), original.body());

            Request request = requestBuilder.build();
            Response response = null;
//            try {
                 response = chain.proceed(request);
//            }catch (SocketTimeoutException timeoutException){
//                response = chain.proceed(request);
//            }
           return response;
        });

        OkHttpClient client = httpClientBuilder.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }



}
