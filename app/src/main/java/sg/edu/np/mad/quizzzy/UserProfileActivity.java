package sg.edu.np.mad.quizzzy;

import static sg.edu.np.mad.quizzzy.Classes.TOTPUtil.verifyTOTP;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.edu.np.mad.quizzzy.Classes.QRCodeUtil;
import sg.edu.np.mad.quizzzy.Classes.TOTPUtil;
import sg.edu.np.mad.quizzzy.Flashlets.CreateFlashlet;
import sg.edu.np.mad.quizzzy.Flashlets.FlashletList;
import sg.edu.np.mad.quizzzy.Models.User;



public class UserProfileActivity extends AppCompatActivity {

    public static class secret {
        public static String secret = TOTPUtil.generateSecretKey();
    }

    Gson gson = new Gson();

    // Data Variables
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle Back Navigation Toolbar
        Toolbar toolbar = findViewById(R.id.uPViewToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.this.getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Handle Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0, 0, 0, 0);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.create) {
                    Intent createFlashletIntent = new Intent(getApplicationContext(), CreateFlashlet.class);
                    createFlashletIntent.putExtra("userId", "");
                    startActivity(createFlashletIntent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.flashlets) {
                    startActivity(new Intent(getApplicationContext(), FlashletList.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.stats) {
                    startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        // Get User from Intent
        Intent receiveIntent = getIntent();
        user = gson.fromJson(receiveIntent.getStringExtra("userJSON"), User.class);

        // get UI elements
        TextView usernameLbl = findViewById(R.id.uPUsername);
        TextView flashletCountLbl = findViewById(R.id.uPFlashletCount);
        Button register2FA = findViewById(R.id.register2FA);

        usernameLbl.setText(user.getUsername());
        String flashletCount = user.getCreatedFlashlets().size() + " Flashlets";
        flashletCountLbl.setText(flashletCount);
        String secret = UserProfileActivity.secret.secret;


        register2FA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.register2fa_popup, null);
                PopupWindow popupWindow = new PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setElevation(5.0f);
                ImageView qrCodeImageView = popupView.findViewById(R.id.qrCodeImageView);
                Button closeButton = popupView.findViewById(R.id.close_button);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                // Set a dim background behind the popup
                popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                popupWindow.setOutsideTouchable(true);

                // Show the popup window at the center of the layout
                popupWindow.showAtLocation(v, android.view.Gravity.CENTER, 0, 0);

                // Dim the background
                View container = popupWindow.getContentView().getRootView();
                if (container != null) {
                    WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                    WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                    p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    p.dimAmount = 0.5f;
                    if (wm != null) {
                        wm.updateViewLayout(container, p);
                    }
                }

                EditText pin1 = popupView.findViewById(R.id.pin1);
                EditText pin2 = popupView.findViewById(R.id.pin2);
                EditText pin3 = popupView.findViewById(R.id.pin3);
                EditText pin4 = popupView.findViewById(R.id.pin4);
                EditText pin5 = popupView.findViewById(R.id.pin5);
                EditText pin6 = popupView.findViewById(R.id.pin6);

                pin1.addTextChangedListener(new TOTPWatcher(pin1, pin2, popupView));
                pin2.addTextChangedListener(new TOTPWatcher(pin2, pin3, popupView));
                pin3.addTextChangedListener(new TOTPWatcher(pin3, pin4, popupView));
                pin4.addTextChangedListener(new TOTPWatcher(pin4, pin5, popupView));
                pin5.addTextChangedListener(new TOTPWatcher(pin5, pin6, popupView));
                pin6.addTextChangedListener(new TOTPWatcher(pin6, null, popupView));


                String issuer = "Quizzzy";
                String account = "user@example.com";
                String totpUri = TOTPUtil.getTOTPURI(secret, issuer, account);


                try {
                    Log.d("debug", totpUri);
                    Bitmap qrCodeBitmap = QRCodeUtil.generateQRCode(totpUri);
                    qrCodeImageView.setImageBitmap(qrCodeBitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private class TOTPWatcher implements TextWatcher {

        private View currentView;
        private View nextView;
        private View popupView;

        public TOTPWatcher(View currentView, View nextView, View popupView) {
            this.currentView = currentView;
            this.nextView = nextView;
            this.popupView = popupView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus(); // Move focus to next EditText
            } else if (s.length() == 0 && currentView != null) {
                currentView.requestFocus(); // Stay on the current EditText
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (areAllPinFieldsFilled()) {
                String secret = UserProfileActivity.secret.secret;
                EditText pin1 = popupView.findViewById(R.id.pin1);
                EditText pin2 = popupView.findViewById(R.id.pin2);
                EditText pin3 = popupView.findViewById(R.id.pin3);
                EditText pin4 = popupView.findViewById(R.id.pin4);
                EditText pin5 = popupView.findViewById(R.id.pin5);
                EditText pin6 = popupView.findViewById(R.id.pin6);
                String totp = pin1.getText().toString() +
                        pin2.getText().toString() +
                        pin3.getText().toString() +
                        pin4.getText().toString() +
                        pin5.getText().toString() +
                        pin6.getText().toString();
                boolean isValid = verifyTOTP(secret, totp);
                if (!isValid) {
                    FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore firebase = FirebaseFirestore.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    Map<String, Object> firebaseSecret = new HashMap<>();
                    firebaseSecret.put("2faSecret", secret);


                    firebase.collection("users").document(currentUser.getUid()).update(firebaseSecret);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Invalid TOTP", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private boolean areAllPinFieldsFilled() {
            EditText pin1 = popupView.findViewById(R.id.pin1);
            EditText pin2 = popupView.findViewById(R.id.pin2);
            EditText pin3 = popupView.findViewById(R.id.pin3);
            EditText pin4 = popupView.findViewById(R.id.pin4);
            EditText pin5 = popupView.findViewById(R.id.pin5);
            EditText pin6 = popupView.findViewById(R.id.pin6);

            return !pin1.getText().toString().isEmpty() &&
                    !pin2.getText().toString().isEmpty() &&
                    !pin3.getText().toString().isEmpty() &&
                    !pin4.getText().toString().isEmpty() &&
                    !pin5.getText().toString().isEmpty() &&
                    !pin6.getText().toString().isEmpty();
        }
    }
}
