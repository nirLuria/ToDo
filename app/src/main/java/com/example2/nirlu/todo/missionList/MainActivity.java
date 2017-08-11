package com.example2.nirlu.todo.missionList;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;





public class MainActivity extends AbsRuntimePermissions
{
    //testttt

    private static int SPLASH_TIME_OUT=1000;



    private static DataBaseHelper myDb;
    private DatabaseReference mRootRef;

    Typeface buttonFont;
    Typeface alertDialogFont;
    private static int REQUEST_PERMISSION = 10;
    private EditText inputUserName;
    private EditText inputCode;
    private static ListView listView;
    private ArrayList<String> groupsArray = new ArrayList<String>();

    private SharedPreferences mPreferences;
    private String myPhoneNumber;
    Button sendSMS;
    private final static int PICK_CONTACT = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        if (getIntent().getBooleanExtra("LOGOUT", false))
        {
            finish();
        }

        super.onCreate(savedInstanceState);

        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //

        setContentView(R.layout.welcome_screen);


        getGroups();


        //welcome screen.
        //
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
                intent.putExtra("firstLogin", "true");
                startActivity(intent);
                finish();


            }
        },SPLASH_TIME_OUT);
        //



        //    optionBar();

        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");
        alertDialogFont= Typeface.createFromAsset(getAssets(), "dragon-webfont.ttf");

        myDb = new DataBaseHelper(this);

        //ask for permissions.
        requestAppPermissions(new String[]
                {
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_CONTACTS,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.READ_SMS,
                        android.Manifest.permission.SEND_SMS,

                }, R.string.msg, REQUEST_PERMISSION);

        //authentication.
        mPreferences = getSharedPreferences("User", MODE_PRIVATE);


        //execute methods.
        //     exitButtonClickListener();
        //     newGroupClickListener();
        //     viewGroupsClickListener();
        getGroups();

        //logOut();
        authenticationManager();



    }


    /*

        public void showNotification(View v)
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.logo);
            builder.setContentTitle("Nir's notification");
            builder.setContentText("This is my great not...");
            Intent intent = new Intent(this, SecondClass.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(SecondClass.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManager NM= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NM.notify(0,builder.build());
        }

        //display options menu bar.
        public boolean onCreateOptionsMenu(Menu menu)
        {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.main_main_activity, menu);
            return super.onCreateOptionsMenu(menu);
        }


        private void optionBar()
        {
            ActionBar ab = getSupportActionBar();
            ab.setLogo(R.drawable.logo);
            ab.setDisplayUseLogoEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
            ab.setTitle("2Do");
            ab.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        }


        //the menu bar itself.
        public boolean onOptionsItemSelected(MenuItem item)
        {
            switch(item.getItemId())
            {
                case R.id.info_id:
                    Intent intent = new Intent("com.example2.nluria.missionList.Info");
                    startActivity(intent);
                case R.id.action_settings:
                    Toast.makeText(getApplicationContext(), "setting icon is selected", Toast.LENGTH_SHORT).show();
                default:
                    return super.onOptionsItemSelected(item);
            }
        }




    */
    //function for fixing a bug in retrieving data in the first click.
    public void getGroups()
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups");

        listView = (ListView)findViewById(R.id.groupsListView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, groupsArray );

        //   listView.setAdapter(adapter);

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s)
            {
                final String groupToCheck=dataSnapshot.getKey();

                ///
                DatabaseReference mRootRefTemp = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users/" + myPhoneNumber+"/");
                mRootRefTemp.addChildEventListener(new ChildEventListener()
                {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshotSon, String s)
                    {
                        //show me only groups that i belong to.
                        if (dataSnapshotSon.child(groupToCheck).getValue() != null)
                        {
                            for (DataSnapshot child: dataSnapshot.getChildren())
                            {
                                if (child.getKey().toString().equals("nameOfGroup"))
                                {
                                    String value = child.getValue().toString();
                                    groupsArray.add(value);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                ///


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }



    @Override
    public void onPermissionsGranted(int requestCode)
    {
        //   Toast.makeText(getApplicationContext(), "זיהוי משתמש עבר בהצלחה!", Toast.LENGTH_LONG).show();
    }


    //authentication
    public void authenticationManager()
    {
        if (mPreferences.contains("username"))
        {
            // start Main activity
            myPhoneNumber=mPreferences.getString("username","");
        }
        else
        {
            // ask him to enter his credentials
            authentication();
        }
    }


    public void authentication()
    {
        //print error message to screen.
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        inputUserName = new EditText(this);
        alert_builder.setView(inputUserName);
        alert_builder.setMessage("(זהו תהליך חד פעמי)")
                // alert_builder.setMessage("This is only one time process.")
                .setCancelable(false)
                .setPositiveButton("לא תודה, אנסה בפעם אחרת", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        finish();
                    }
                })
                .setNegativeButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = mPreferences.edit();

                        editor.putString("username", inputUserName.getText().toString());
                        editor.commit();
                        myPhoneNumber=inputUserName.getText().toString();
                        if (myPhoneNumber.matches("[0-9]+") && myPhoneNumber.length() ==10)
                        {
                            dialogInterface.cancel();
                            int genNum=generateNumber();
                            sendSms(myPhoneNumber, genNum);
                            checkSmsCode(genNum);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"illegal number", Toast.LENGTH_LONG).show();
                            authentication();
                        }

                    }
                });
        AlertDialog alert = alert_builder.create();
        //alert.setTitle("For first use, please enter your phone number");
        alert.setTitle("עבור שימוש ראשוני באפליקציה, בבקשה הזן את מס' הפלאפון שלך:");
        alert.show();
    }


    public void checkSmsCode(final int genNum)
    {
        //print error message to screen.
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        inputCode = new EditText(this);
        alert_builder.setView(inputCode);
        alert_builder.setMessage("בבקשה הזן את הקוד שקיבלת בהודעת טקסט:")
                //alert_builder.setMessage("Please enter below the code you received:")
                .setCancelable(false)
                .setPositiveButton("לא תודה, אנסה בפעם אחרת", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        finish();
                    }
                })
                .setNegativeButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strCode=""+genNum;

                        if (inputCode.getText().toString().equals(strCode))
                        {
                            Toast.makeText(getApplicationContext(),"הרישום לאפליקציה עבר בהצלחה!", Toast.LENGTH_LONG).show();
                            dialogInterface.cancel();
                            refreshActivity();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"קוד שגוי, אנא נסה שנית", Toast.LENGTH_LONG).show();
                            checkSmsCode(genNum);
                        }

                    }
                });
        AlertDialog alert = alert_builder.create();
        // alert.setTitle("Code Authentication");
        alert.setTitle("אימות קוד");
        alert.show();
    }


    public void sendSms(final String number, int genNum)
    {
        //String message = "Hello, please use the following code for access to my great app: "+genNum;
        String message = "שלום! בבקשה השתמש/י בקוד הבא בכדי להתחיל ולהשתמש באפליקציה: "+genNum;

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, message, null, null);
        Toast.makeText(getApplicationContext(),"הודעת טקסט נשלחה בהצלחה ל: " +number, Toast.LENGTH_LONG).show();
    }


    public int generateNumber()
    {
        int min = 1000;
        int max = 9999;

        Random r = new Random();
        int generatedNum = r.nextInt(max - min + 1) + min;
        //int generatedNum=1111;
        System.out.println("generatedNum: "+ generatedNum);

        return generatedNum;
    }


    public void logOut()
    {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();   // This will delete all your preferences, check how to delete just one
        editor.commit();
    }


    public void refreshActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    /*
    public void onBackPressed()
    {
        //there is no way back from the main activity.
    }
    */
}
