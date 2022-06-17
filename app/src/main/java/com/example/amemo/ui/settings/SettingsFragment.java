package com.example.amemo.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;

import com.example.amemo.CacheHandler;
import com.example.amemo.R;
import com.example.amemo.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        SharedPreferences sp = getContext().getSharedPreferences("root_preferences", Context.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener((preferences, key) -> {
            switch (key) {
                case "is_opened_note":
                    if (preferences.getBoolean(key, false)) {
                        System.out.println("Global level switched to 0.");
                        CacheHandler.user.globalLevel = 0;
                    } else if (preferences.getBoolean("note_method", false)) {
                        System.out.println("Global level switched to 2.");
                        CacheHandler.user.globalLevel = 2;
                    } else {
                        System.out.println("Global level switched to 1.");
                        CacheHandler.user.globalLevel = 1;
                    }
                    break;
                case "note_method":
                    if (preferences.getBoolean(key, false)) {
                        System.out.println("Global level switched to 2.");
                        CacheHandler.user.globalLevel = 2;
                    } else {
                        System.out.println("Global level switched to 1.");
                        CacheHandler.user.globalLevel = 1;
                    }
                    break;
            }
        });

        if (savedInstanceState == null) {

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment.SetSettingsFragment())
                    .commit();
        }

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SetSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}