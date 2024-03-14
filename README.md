## Bug report

There's a problem with Espresso tests idling for 5s after transitioning
from StartActivity to SecondActivity on API 31+. It seems to be ok on API 30.
It's not a test or app code issue, but a race condition in the Instrumentation code.

The Instrumentation runs on its own thread, so it has a set of synchronization mechanisms to wait for stuff happening in the app,
and sync the test code with the app code.
At some point in the test flow, `Instrumentation::startActivitySync` wants to wait for Activity entry [animation to complete](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/app/Instrumentation.java;drc=549506b4c3cfb21f153e4913c280020e8b2e6f78;l=446).
It waits for 5s, unless
- `Activity.mEnterAnimationComplete` is true
- `Instrumentation.mAnimationCompleteLock` received a signal that the animation has already completed
  
The problem is the following:
The animations are disabled, so the animation is complete as soon as the Activity is started,
setting `mEnterAnimationComplete` to true.
The StartActivity starts the SecondActivity, and StartActivity goes into onStop.
For whatever reason, the platform sets `mEnterAnimationComplete` [to false in onStop](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/app/Activity.java;l=2720).
After this, we get into `Instrumentation::waitForEnterAnimationComplete`.

So the first condition can't be met at this point.
The second condition also can't be met, because the animation was 0s long and mAnimationCompleteLock got a signal before
`waitForEnterAnimationComplete` started waiting on it.
(What's interesting is that in some cases, on API 30 in particular, this race condition doesn't happen, apparently)
This results in `waitForEnterAnimationComplete` waiting for 5s after StartActivity starts the OverviewActivity.

To prove the point, I've used a reflection hack to set `mEnterAnimationComplete` to true after onStop,
so that the first condition is met and the waiting doesn't even start.
This fixed the 5s idling even in an empty test scenario.
