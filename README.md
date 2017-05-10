# XYZ Reader

### Overview

The purpose of this project is to style an existing application according to the material design guidelines. 

### Screenshots

![GridPhone](screenshots/n5_list_port.png?raw=true) ![DetailPhone](screenshots/n5_detail_port.png?raw=true)

![GridTablet](screenshots/n9_grid_port.png?raw=true) ![DetailTablet](screenshots/n9_detail_port.png?raw=true)

### Development Tasks

> App uses the Design Support library and its provided widget types (FloatingActionButton, AppBarLayout, SnackBar, etc).

+ All of the mentioned elements were incorporated. 

> App uses CoordinatorLayout for the main Activity.

+ The main list activity and detail activity use `CoordinatorLayout` to incorporate a `CollapsingToolbarLayout`.

> App theme extends from AppCompat.

+ Base theme extends from `Theme.AppCompat.Light.DarkActionBar`.

> App uses an AppBar and associated Toolbars.

+ List activity and detail activity both incorporate an `AppBarLayout`.

> App provides a Floating Action Button for the most common action(s).

+ A floating action button was used for the sharing action and has been placed appropriately for tablet and phone layouts. On handsets the FAB will animate out when scrolling down through the text so no information is obscured.

> App properly specifies elevations for app bars, FABs, and other elements specified in the Material Design specification.

+ Default theme values were used throughout and where not applicable guidance from the [specification](https://material.io/guidelines/material-design/elevation-shadows.html#elevation-shadows-elevation-android) was used.

> App has a consistent color theme defined in styles.xml. Color theme does not impact usability of the app.

+ Incorported the `Palette` library to generate a color theme for the detail activity.  

> App provides sufficient space between text and surrounding elements.

+ Spacing follows the material design grid rules (8dp square baseline grid). 

> App uses images that are high quality, specific, and full bleed.

+ Large header image was used with a 16:9 aspect ratio where possible. 

> App uses fonts that are either the Android defaults, are complementary, and aren't otherwise distracting.

+ Only Android default fonts were used. 