package com.marian.movies.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.marian.movies.app.fragment.MovieDetails;
import com.marian.movies.app.fragment.MyNewFragment;
import com.marian.movies.app.R;



public class MainActivity extends AppCompatActivity implements MovieDetails.OnFragmentInteractionListener{


    public void openHome(View view) {
        System.out.println("Success");
    }

    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            MyNewFragment firstFragment = new MyNewFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, firstFragment).addToBackStack(null).commit();
        }


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
        if (id == R.id.highest_rated) {
            MyNewFragment newFragment = new MyNewFragment();

            Bundle args = new Bundle();

            args.putString("SortBy","highest-rated");

            newFragment.setArguments(args);

            android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }

        if (id == R.id.most_popular) {

            MyNewFragment newFragment = new MyNewFragment();

            Bundle args = new Bundle();

            args.putString("SortBy","popularity.desc");

            newFragment.setArguments(args);

            android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }

        if (id == R.id.favorites) {

            MyNewFragment newFragment = new MyNewFragment();

            Bundle args = new Bundle();

            args.putString("SortBy","favorites");

            newFragment.setArguments(args);

            android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
