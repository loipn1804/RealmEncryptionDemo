package liophan.realmencryptiondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.txtLog)
    TextView txtLog;

    private Realm mRealm;

    private RealmResults<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
        mUsers = mRealm.where(User.class).findAllSorted("userId");
        mUsers.addChangeListener(new RealmChangeListener<RealmResults<User>>() {
            @Override
            public void onChange(RealmResults<User> element) {
                String log = "";
                for (User user : element) {
                    log += user.getUserId() + " " + user.getName() + "\n";
                }
                txtLog.setText(log);
            }
        });

        String log = "";
        for (User user : mUsers) {
            log += user.getUserId() + " " + user.getName() + "\n";
        }
        txtLog.setText(log);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(mUsers.size() + 1, edtName.getText().toString().trim());
                mRealm.beginTransaction();
                mRealm.insert(user);
                mRealm.commitTransaction();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }
}
