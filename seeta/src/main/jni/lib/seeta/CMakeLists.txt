set( CMAKE_CXX_STANDARD 11 )

set( SEETA_SOURCES
        src/util/nms.cpp
        src/util/image_pyramid.cpp
        src/io/lab_boost_model_reader.cpp
        src/io/surf_mlp_model_reader.cpp
        src/feat/lab_feature_map.cpp
        src/feat/surf_feature_map.cpp
        src/classifier/lab_boosted_classifier.cpp
        src/classifier/mlp.cpp
        src/classifier/surf_mlp.cpp
        src/face_detection.cpp
        src/fust.cpp )

add_library( seeta_facedct_lib SHARED ${SEETA_SOURCES} )