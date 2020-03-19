# Android SimpleSpinnerView
<img src ="https://img.shields.io/badge/version-0.0.2-brightgreen.svg"/> [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Gradle
``` gradle
allprojects {
   repositories {
     ...
      maven { url "https://jitpack.io" }
    }
}
```
``` gradle
databinding { 
    enabled = true
}

dependencies {
    implementation 'com.github.hyeyeon2371:SimpleSpinnerView:<Latest_Version>'
}
```

## Usage
1. Add layout
``` xml
 <com.github.hyeyeon2371.SimpleSpinnerView
          ... 
          app:visibleCount="5"
          app:placeHolder="Hello World!"
          app:placeHolderColor="@color/colorAccent"
          app:textSize="14sp"
          app:textColor="@color/colorPrimary"
          app:isDisabled="false"
  />
```
2. Configure attrs
- placeHolder: The text of the place holder
- placeHolderColor: The color of the place holder
- textColor: The color of list items
- textSize: The size of list items
- isDisabled: The boolean of whether or not it is disabled 
- visibleCount: The count of visible list items 

3. Configure event listener
``` kotlin
val list = mutableListOf("1","2", .. ,"7","8","9")
mSimpleSpinnerView.setItems(list)
mSimpleSpinnerView.setOnItemClickTask { position -> /* event */ }

or 

mSimpleSpinnerView.setAdapter(object : BaseAdapter(){..})
```

## Screenshot
<img src="https://user-images.githubusercontent.com/42951723/71900989-4b89b380-31a2-11ea-8034-5596ada56c1e.gif" width="250px"/>

## License 
<a href="https://github.com/hyeyeon2371/SimpleSpinnerView/blob/master/LICENSE">MIT</a>
