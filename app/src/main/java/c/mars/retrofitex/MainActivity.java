package c.mars.retrofitex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.t)
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        t.setOnClickListener(v->{
            t.setText("clicked");
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://api.github.com")
                    .build();
            GitHubService service = restAdapter.create(GitHubService.class);
            Observable<Response> rs = service.listRepos("octocat");
            rs.subscribe(new Subscriber<Response>() {
                             @Override
                             public void onCompleted() {
                                 t.append("\ncompleted");
                             }

                             @Override
                             public void onError(Throwable e) {
                                 t.append("\n" + e.getMessage());
                             }

                             @Override
                             public void onNext(Response response) {
                                 t.append("\n" + response.getBody().toString());
                             }
                         });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    interface GitHubService {
        @GET("/users/{user}/repos")
        Observable<Response> listRepos(@Path("user") String user);
    }
}
