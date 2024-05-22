# CaribEViz
![CaribEViz Splash](spalsh.bmp)
This is a fork of Ergo (https://opensource.ncsa.illinois.edu/bitbucket/scm/ergo/ergo.git) developed by the National Center for Supercomputing Applications, University of Illinois at Urbana Champagne. 
Most of the developement occurred at the University of the West Indies, Seismic Research Center (UWI, SRC) under the the UWI, Institute for Sustainable Development executed Enhancing Knowledge and Application of Comprehensive Disaster Manager Initiative (EKACDM). The software has been used to estimate, in a medium to high-resolution scale, the seismic risk of the Kingston Metropolitan Area and Portmore, Jamaica, Barbados, and Dominica.
Ergo is an open source implementation of HAZUS. CaribEViz was developed to work with building footprints, data sets common to most CSOs of island-states under the remit or adjacent to UWI, and local expert knowledge to produce building structural classifcation. Analysese that have been validate are:  
1. Building Structure and Occupancy Type\Rate Classification
2. Building Structural Damage
3. Direct Econoic Loss
4. UWI Dislocated Population (using the EKACDM methodology)
5. UWI Casualities (using the EKACDM methodology)
6. Shelter Post-Earthquake Capacity
7. Healthcare Facility Post-Earthquake Capacity

A manuscript is being prepared for Natural Hazards that will detail the methodologies implemented in CaribEViz.

Developement of CaribEViz is on needs basis or bug fixing. 

Ergo currently requires Eclipse RCP Mars and the ancient geotools v14. Efforts will be made in the near future to upgrade Ergo (FYI Ergo will soon be moved to [github](https://github.com/ncsa)). Bug fixes and improvements made to Ergo source will then be merged and CaribEViz will be updated to the latest geotools. In the meantime the development environment (including packages from defunct Maven repositories) and workspace are included here. This is anethema to Eclipse RCP development. After cloning this project run the executable com.machelhiggins.SetupCaribEvizWorkspace that will fix workspace classpaths and move the relevant sundry.

If you want to get running, download any of the install binaries with relevant region data under installs. 
For questions and quicker answers, join the CaribEViz user group: https://groups.google.com/g/caribeviz-user-group/

CaribEViz code and any resulting analyses is offered as is. CaribEViz is licensed under the GNU General Public License.  Ergo is provided under the terms and conditions of the Mozilla Public License, Version 2.0.
