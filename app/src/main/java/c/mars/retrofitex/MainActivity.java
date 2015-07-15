package c.mars.retrofitex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.auth.AUTH;

import java.io.IOException;
import java.nio.Buffer;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Data;
import okio.ByteString;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.t)
    TextView t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        t.setMovementMethod(new ScrollingMovementMethod());

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://rewards.mymodlet.com")//https://api.github.com")
                .build();
        RewardsService service = restAdapter.create(RewardsService.class);
        Observable<AuthResponse> responseObservable = service.login("user", "password", "password");
        responseObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe((AuthResponse authResponse) -> {
                            t.append("\n" + authResponse.toString());
                            Observable<Response> response = service.summary("bearer " + authResponse.access_token);
                            response.observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            (Response r) -> {
                                                String s = "";
                                                try {
                                                    s = ByteString.read(r.getBody().in(), (int) r.getBody().length()).utf8();
                                                } catch (IOException e) {
                                                    s = "Problem when convert string";
                                                }

//                                            strip backslashes and quotes
                                                s = s.replaceAll("\\\\", "");
                                                if (s.startsWith("\"")) {
                                                    s = s.substring(1);
                                                }
                                                if (s.endsWith("\"")) {
                                                    s = s.substring(0, s.length() - 1);
                                                }
//                                                parse json manually with gson
                                                Summary summary = new Gson().fromJson(s, Summary.class);
                                                t.append("\n" + summary.toString());
                                            },
                                            (e) -> {
                                            },
                                            () -> t.append("\ncompleted")
                                    );
                        },
                        (e) -> t.append("\n" + e.toString()),
                        () -> t.append("\ncompleted")
                );
    }

    interface GitHubService {
        @GET("/users/{user}/repos")
        Observable<List<Repo>> listRepos(@Path("user") String user);
    }

    interface RewardsService {
        @FormUrlEncoded
        @POST("/token")
        Observable<AuthResponse> login(@Field("username") String username, @Field("password") String password, @Field("grant_type") String grant_type);

        @GET("/api/summary")
        Observable<Response> summary(@Header("Authorization") String header);
    }

    @Data
    class Repo {
        int id;
        String name;
    }

    @Data
    class AuthResponse {
        String access_token;
    }

    @Data
    class Summary {
        long totalPoints;
        Activity latestActivity;

        @Data
        class Activity {
            String date;
        }
    }
}
