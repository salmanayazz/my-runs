package com.example.myruns

// Generated with Weka 3.8.6
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Thu Nov 23 16:09:37 PST 2023
internal object WekaClassifier {
    @Throws(Exception::class)
    fun classify(i: Array<Any?>): Double {
        var p = Double.NaN
        p = N408b90850(i)
        return p
    }

    fun N408b90850(i: Array<Any?>): Double {
        var p = Double.NaN
        if (i[0] == null) {
            p = 0.0
        } else if ((i[0] as Double?)!! <= 13.390311) {
            p = 0.0
        } else if ((i[0] as Double?)!! > 13.390311) {
            p = N3cc779fa1(i)
        }
        return p
    }

    fun N3cc779fa1(i: Array<Any?>): Double {
        var p = Double.NaN
        if (i[64] == null) {
            p = 1.0
        } else if ((i[64] as Double?)!! <= 14.534508) {
            p = N761efa62(i)
        } else if ((i[64] as Double?)!! > 14.534508) {
            p = 2.0
        }
        return p
    }

    fun N761efa62(i: Array<Any?>): Double {
        var p = Double.NaN
        if (i[4] == null) {
            p = 1.0
        } else if ((i[4] as Double?)!! <= 14.034383) {
            p = N5bc51cc33(i)
        } else if ((i[4] as Double?)!! > 14.034383) {
            p = 1.0
        }
        return p
    }

    fun N5bc51cc33(i: Array<Any?>): Double {
        var p = Double.NaN
        if (i[7] == null) {
            p = 1.0
        } else if ((i[7] as Double?)!! <= 4.804712) {
            p = 1.0
        } else if ((i[7] as Double?)!! > 4.804712) {
            p = 2.0
        }
        return p
    }
}