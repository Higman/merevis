package com.github.higman.merevis_file_info_manager.java.nodes

import com.github.higman.merevis_file_info_manager.Name
import com.github.higman.merevis_file_info_manager.Package
import com.github.higman.merevis_file_info_manager.java.JavaVisibility

class JavaPackage(name: Name): Package(name, visibility = JavaVisibility.PUBLIC)