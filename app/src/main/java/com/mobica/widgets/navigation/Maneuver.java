package com.mobica.widgets.navigation;

import com.mobica.widgets.R;

enum Maneuver {
    RoundLeft(R.id.navManeuverImage1, R.id.navThenManeuverImage1),
    RoundRight(R.id.navManeuverImage2, R.id.navThenManeuverImage2),
    RoundStright(R.id.navManeuverImage3, R.id.navThenManeuverImage3),
    Stright(R.id.navManeuverImage4, R.id.navThenManeuverImage4),
    TurnLeftSlight(R.id.navManeuverImage5, R.id.navThenManeuverImage5),
    TurnLeft(R.id.navManeuverImage6, R.id.navThenManeuverImage6),
    TurnLeftSharp(R.id.navManeuverImage7, R.id.navThenManeuverImage7),
    TurnUTurnLeft(R.id.navManeuverImage8, R.id.navThenManeuverImage8),
    TurnRightSlight(R.id.navManeuverImage9, R.id.navThenManeuverImage9),
    TurnRight(R.id.navManeuverImage10, R.id.navThenManeuverImage10),
    TurnRightSharp(R.id.navManeuverImage11, R.id.navThenManeuverImage11),
    TurnUTurnRight(R.id.navManeuverImage12, R.id.navThenManeuverImage12);

    public int viewId;
    public int smallViewId;

    Maneuver(int viewId, int smallViewId) {
        this.viewId = viewId;
        this.smallViewId = smallViewId;
    }
}
