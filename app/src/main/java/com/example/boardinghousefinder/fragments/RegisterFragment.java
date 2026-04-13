package com.example.boardinghousefinder.fragments;

import android.os.Bundle;
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

public class RegisterFragment extends Fragment {

    public RegisterFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView textTitle = view.findViewById(R.id.textTitle);
        TextView textSubtitle = view.findViewById(R.id.textSubtitle);
        TextView switchText = view.findViewById(R.id.textSwitch);
        Button submitBtn = view.findViewById(R.id.btnSubmit);
        RadioGroup roleGroup = view.findViewById(R.id.radioRoleGroup);

        // CHANGE UI TEXT
        textTitle.setText("Create Account");
        textSubtitle.setText("Register to continue");
        submitBtn.setText("Register");
        switchText.setText("Already have an account? Login");

        // SWITCH BACK TO LOGIN
        switchText.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // REGISTER ACTION
        submitBtn.setOnClickListener(v -> {

            EditText emailInput = view.findViewById(R.id.emailInput);
            EditText passwordInput = view.findViewById(R.id.passwordInput);

            String username = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            int selectedId = roleGroup.getCheckedRadioButtonId();
            String role = (selectedId == R.id.radioOwner) ? "owner" : "renter";

            String url = "http://192.168.254.104/casptone/register.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getString("status").equals("success")){
                                
                                // SAVE USER DATA TO SESSION
                                SessionManager.setUserId(obj.getInt("id"));
                                SessionManager.setUsername(obj.optString("username", username));
                                SessionManager.setUserRole(obj.getString("role"));
                                SessionManager.setEmail(obj.optString("email", ""));
                                SessionManager.setLoggedIn(true);

                                Toast.makeText(getContext(), "Registered as " + SessionManager.getUserRole(), Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();

                            } else {
                                Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show()
            ){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    params.put("role", role);
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        });

        return view;
    }
}
