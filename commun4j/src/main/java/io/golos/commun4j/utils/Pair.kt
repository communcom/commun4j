/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */


package io.golos.commun4j.utils


/**
 * Represents a generic pair of two values.
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Pair exhibits value semantics, i.e. two pairs are equal if both components are equal.
 *
 * An example of decomposing it into values:
 * @sample samples.misc.Tuples.pairDestructuring
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @property first First value.
 * @property second Second value.
 * @constructor Creates a new instance of Pair.
 */
public data class Pair<out A, out B>(
        public val first: A,
        public val second: B
) {

    /**
     * Returns string representation of the [Pair] including its [first] and [second] values.
     */
    public override fun toString(): String = "($first, $second)"
}

