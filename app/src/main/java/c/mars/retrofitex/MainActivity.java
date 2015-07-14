package c.mars.retrofitex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Data;
import retrofit.RestAdapter;
import retrofit.http.GET;
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
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://api.github.com")
                    .build();
            GitHubService service = restAdapter.create(GitHubService.class);
            Observable<List<Repo>> repos = service.listRepos("c-mars");

            repos.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Repo>>() {
                @Override
                public void onCompleted() {
                    t.append("\n" + "completed");
                }

                @Override
                public void onError(Throwable e) {
                    t.append("\n" + e.toString());
                }

                @Override
                public void onNext(List<Repo> repos) {
                    t.append("\n" + repos.toString());
                }
            });
    }

    interface GitHubService {
        @GET("/users/{user}/repos")
        Observable<List<Repo>> listRepos(@Path("user") String user);
    }

    @Data
    class Repo{
        int id;
        String name;
    }
}
