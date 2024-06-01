package sg.edu.np.mad.quizzzy.Flashlets;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import sg.edu.np.mad.quizzzy.Classes.ClassDetail;
import sg.edu.np.mad.quizzzy.HomeActivity;
import sg.edu.np.mad.quizzzy.LoginActivity;
import sg.edu.np.mad.quizzzy.Models.Flashcard;
import sg.edu.np.mad.quizzzy.Models.Flashlet;
import sg.edu.np.mad.quizzzy.Models.SQLiteManager;
import sg.edu.np.mad.quizzzy.Models.User;
import sg.edu.np.mad.quizzzy.Models.UserWithRecents;
import sg.edu.np.mad.quizzzy.R;

public class CreateClassFlashlet extends AppCompatActivity {
    // Initialisation of Firebase Cloud Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Gson gson = new Gson();

    // Data Variables
    Flashlet newFlashlet;
    ArrayList<Flashcard> flashcards = new ArrayList<Flashcard>();

    // View Variables
    private Button addNewFlashcardBtn;
    private Button createFlashletBtn;
    private LinearLayout flashcardListView;
    private View newFlashcardView;
    private EditText createFlashletTitle;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_flashlet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashTitle), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle Back Navigation Toolbar
        Toolbar toolbar = findViewById(R.id.cFToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateClassFlashlet.this.getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.create);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0,0,0,0);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.create) {
                    return true;
                } else if (itemId == R.id.flashlets) {
                    startActivity(new Intent(getApplicationContext(), FlashletList.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.stats) {
                    // TODO: Integrate Darius's Part
                    return true;
                }
                return false;
            }
        });

        // Get intent data
        Intent receivingIntent = getIntent();
        String classId = receivingIntent.getStringExtra("classId");
        String userId = receivingIntent.getStringExtra("userId");

        // Listen for onClick on 'Create Flashlet' button, then create the flashlet
        addNewFlashcardBtn = findViewById(R.id.cFAddNewFlashcardBtn);
        createFlashletBtn = findViewById(R.id.cFCreateNewFlashletButton);
        flashcardListView = findViewById(R.id.cFCreateNewFlashcardList);
        addNewFlashcardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new Flashcard object, and add to ArrayList
                Flashcard newFlashcard = new Flashcard("", "");

                // Inflate the Item for a new Flashcard
                newFlashcardView = LayoutInflater.from(CreateClassFlashlet.this).inflate(R.layout.create_flashlet_newflashcard, null, false);

                // Listen for updates in the Flashcard Info
                EditText keywordEditText = newFlashcardView.findViewById(R.id.newFlashcardKeywordInput);
                keywordEditText.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        newFlashcard.setKeyword(keywordEditText.getText().toString());
                    }
                });
                EditText definitionEditText = newFlashcardView.findViewById(R.id.newFlashcardDefinitionInput);
                definitionEditText.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        newFlashcard.setDefinition(definitionEditText.getText().toString());
                    }
                });
                // Add Flashcard to List
                flashcards.add(newFlashcard);

                // Add Inflated View to LinearLayout Container
                flashcardListView.addView(newFlashcardView);

                // Add Spacer View
                View spacerView = new View(CreateClassFlashlet.this);
                LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        20
                );
                flashcardListView.addView(spacerView, spacerParams);
            }
        });

        // Handle Flashlet creation submission
        createFlashletTitle = findViewById(R.id.cFNewTitle);
        createFlashletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate that all fields are filled in
                String title = createFlashletTitle.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter a title before continuing!", Toast.LENGTH_LONG).show();
                    return;
                }

                // Else, Create the Flashlet
                String id = UUID.randomUUID().toString();
                ArrayList<String> creatorId = new ArrayList<>();
                creatorId.add(userId);
                newFlashlet = new Flashlet(id, title, "", creatorId, null, flashcards, System.currentTimeMillis() / 1000L); // Initialise Flashlet with Empty Description

                if (classId != null) {
                    newFlashlet.setClassId(classId);
                }

                // Disable button to prevent double-adding
                createFlashletBtn.setEnabled(false);
                createFlashletBtn.setText("Loading...");

                // Add Flashlet to Firebase
                db.collection("flashlets")
                        .document(id)
                        .set(newFlashlet)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Add Flashlet to SQLite DB
                                SQLiteManager localDB = SQLiteManager.instanceOfDatabase(CreateClassFlashlet.this);
                                User user = localDB.getUser().getUser();
                                CollectionReference docRef = db.collection("class");
                                docRef.whereIn("id", Collections.singletonList(classId)).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String flashletJson = gson.toJson(document.getData());
                                                JsonObject jsonObject = gson.fromJson( flashletJson, JsonObject.class);
                                                JsonArray jArray = jsonObject.getAsJsonArray("createdFlashlets");
                                                ArrayList<String> createdFlashlets = new ArrayList<String>();
                                                if (jArray != null) {
                                                    for (int i=0;i<jArray.size();i++){
                                                        createdFlashlets.add(String.valueOf(jArray.get(i)).replace("\"", ""));
                                                        }
                                                    }
                                                createdFlashlets.add(newFlashlet.getId());
                                                db.collection("class").document(classId).update("createdFlashlets", createdFlashlets)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                createFlashletBtn.setText("Create Flashlet");
                                                                Toast.makeText(getApplicationContext(), "Flashlet Created!", Toast.LENGTH_LONG).show();
                                                                // Send User back to class Page
                                                                Intent flashletListIntent = new Intent(CreateClassFlashlet.this, ClassDetail.class);
                                                                startActivity(flashletListIntent);
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                createFlashletBtn.setText("Create Flashlet");
                                createFlashletBtn.setEnabled(true);

                                Log.e("Flashlet Creation", e.toString());
                                Toast.makeText(getApplicationContext(), "Failed to Create Flashlet", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}