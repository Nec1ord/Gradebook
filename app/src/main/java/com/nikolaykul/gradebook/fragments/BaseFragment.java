package com.nikolaykul.gradebook.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.nikolaykul.gradebook.di.HasComponent;

public abstract class BaseFragment extends Fragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

}
