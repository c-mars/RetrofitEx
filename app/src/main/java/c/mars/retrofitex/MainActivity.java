package c.mars.retrofitex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.apache.http.auth.AUTH;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Data;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
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
        Observable<AuthResponse> response = service.login("user", "password", "password");

        response.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AuthResponse>() {
                    @Override
                    public void onCompleted() {
                        t.append("\n" + "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        t.append("\n" + e.toString());
                    }

                    @Override
                    public void onNext(AuthResponse response) {
                        t.append("\n" + response.toString());
                    }
                });
    }

    interface GitHubService {
        @GET("/users/{user}/repos")
        Observable<List<Repo>> listRepos(@Path("user") String user);
    }

    interface RewardsService {
        @FormUrlEncoded
        @POST("/token")
        Observable<AuthResponse> login(@Field("username") String username, @Field("password") String password, @Field("grant_type") String grant_type);
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
}
