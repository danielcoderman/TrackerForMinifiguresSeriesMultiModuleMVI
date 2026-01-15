package com.drsapps.trackerforlegominifiguresseries.presentation

import android.app.Activity
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

private const val TAG = "MainViewModelReviewDelegate"

class MainViewModelReviewDelegate {
    private var reviewManager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null
    private var requestReviewFlowInProgress = false

    fun initializeReviewManager(activity: Activity) {
        if (reviewManager == null) {
            reviewManager = ReviewManagerFactory.create(activity)
        }
    }

    fun requestReviewFlow(
        activity: Activity,
        onReviewFlowComplete: () -> Unit
    ) {
        if (!requestReviewFlowInProgress) {
            requestReviewFlowInProgress = true

            // Request a ReviewInfo object in order to start the in-app review flow
            val request = reviewManager?.requestReviewFlow()
            request?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    reviewInfo = task.result

                    // Start the in-app review flow
                    val reviewFlow = reviewManager?.launchReviewFlow(activity, reviewInfo!!)
                    reviewFlow?.addOnCompleteListener {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        onReviewFlowComplete()
                        requestReviewFlowInProgress = false
                        Log.d(TAG, "The in-app review flow has completed.")
                    }
                } else {
                    val exception = task.exception
                    if (exception != null) {
                        Log.e(TAG, "requestReviewFlow: Failed to retrieve a ReviewInfo object\n" +
                                exception.stackTraceToString())
                    } else {
                        Log.e(TAG, "requestReviewFlow: Failed to retrieve a ReviewInfo object")
                    }
                }
            }
        }
    }
}