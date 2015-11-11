package com.nikolaykul.gradebook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nikolaykul.gradebook.di.HasComponent;
import com.nikolaykul.gradebook.di.component.ActivityComponent;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setActivityComponent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected abstract void setActivityComponent();

    protected ActivityComponent getActivityComponent() {
        return getComponent(ActivityComponent.class);
    }

    @SuppressWarnings("unchecked")
    private <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

}
