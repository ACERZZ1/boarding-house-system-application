package com.example.boardinghousefinder.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.boardinghousefinder.R;
import com.example.boardinghousefinder.utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView switchText = view.findViewById(R.id.textSwitch);
        Button submitBtn = view.findViewById(R.id.btnSubmit);
        RadioGroup roleGroup = view.findViewById(R.id.radioRoleGroup);

        submitBtn.setText("Login");
        switchText.setText("Don't have an account? Register");

        switchText.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        submitBtn.setOnClickListener(v -> {
            EditText emailInput = view.findViewById(R.id.emailInput);
            EditText passwordInput = view.findViewById(R.id.passwordInput);

            String usernameInput = emailInput.getText().toString().trim();
            String passwordInputStr = passwordInput.getText().toString().trim();

            if (usernameInput.isEmpty() || passwordInputStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = roleGroup.getCheckedRadioButtonId();
            String role = (selectedId == R.id.radioOwner) ? "owner" : "renter";

            // Using your IP
            String url = "http://192.168.254.104/casptone/login.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("LOGIN_DEBUG", "Server Response: " + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("status").equals("success")) {
                                
                                // Parse data safely using opt (prevents crash if missing)
                                int id = obj.optInt("id", -1);
                                String user = obj.optString("username", usernameInput);
                                String roleRes = obj.optString("role", role);
                                String email = obj.optString("email", "");
                                String phone = obj.optString("phone", "");
                                String address = obj.optString("address", "");

                                // SAVE USER DATA TO SESSION (Reverted Context parameter to match current SessionManager)
                                SessionManager.setUserId(id);
                                SessionManager.setUsername(user);
                                SessionManager.setUserRole(roleRes);
                                SessionManager.setEmail(email);
                                SessionManager.setPhone(phone);
                                SessionManager.setAddress(address);
                                SessionManager.setLoggedIn(true);

                                Toast.makeText(getContext(), "Welcome, " + user, Toast.LENGTH_SHORT).show();
                                
                                // Return to AccountFragment
                                if (isAdded()) {
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                }
                            } else {
                                String msg = obj.optString("message", "Invalid credentials");
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("LOGIN_ERROR", "JSON Parse Error", e);
                            Toast.makeText(getContext(), "Server Error: Unexpected response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("LOGIN_ERROR", "Volley Error: " + error.toString());
                        Toast.makeText(getContext(), "Connection Error. Check your IP: 192.168.254.103", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", usernameInput);
                    params.put("password", passwordInputStr);
                    params.put("role", role);
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        });

        return view;
    }
}
