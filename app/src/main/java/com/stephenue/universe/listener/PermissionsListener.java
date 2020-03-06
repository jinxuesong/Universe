package com.stephenue.universe.listener;

import java.util.List;

public interface PermissionsListener{
    //同意权限
    void onPermissionGranted();
    //拒绝权限
    void onPermissionDenied(List<String> permissions);
}
