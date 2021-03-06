package PubVote

import com.geospock.pubvote.GroupSplitter
import com.geospock.pubvote.people.People
import com.geospock.pubvote.people.People.ANDRE
import com.geospock.pubvote.people.People.ARTEM
import com.geospock.pubvote.people.People.BENITA
import com.geospock.pubvote.people.People.CHARLES
import com.geospock.pubvote.people.People.CHRISTOPH
import com.geospock.pubvote.people.People.DAVID_B
import com.geospock.pubvote.people.People.DAVID_W
import com.geospock.pubvote.people.People.DOM
import com.geospock.pubvote.people.People.JAMES_G
import com.geospock.pubvote.people.People.JON
import com.geospock.pubvote.people.People.KAI
import com.geospock.pubvote.people.People.KATIE
import com.geospock.pubvote.people.People.STEVE
import com.geospock.pubvote.people.People.XAVI
import com.geospock.pubvote.places.Place
import com.geospock.pubvote.places.Place.FORT_SAINT_GEORGE
import com.geospock.pubvote.places.Place.HAYMAKERS
import com.geospock.pubvote.places.Place.POLONIA
import com.geospock.pubvote.places.Place.THE_OLD_SPRING
import com.geospock.pubvote.places.Place.WRESTLERS
import com.geospock.pubvote.voters.StandardVoteInput
import com.geospock.pubvote.voters.WeightedRandomVoter

typealias Group = List<People>

fun main(args: Array<String>) {

    val votes: List<Vote> = listOf(
            STEVE voted {
                WRESTLERS += 10
            },
            DAVID_W voted {
                HAYMAKERS += 5
                WRESTLERS += 5
            },
            KAI voted {
                THE_OLD_SPRING +=5
                POLONIA += 5
            },
            JAMES_G voted {
                WRESTLERS += 5
                FORT_SAINT_GEORGE += 5
            },
            ANDRE voted {
                WRESTLERS += 5
                THE_OLD_SPRING += 5
            },
            BENITA voted {
                WRESTLERS += 5
                THE_OLD_SPRING += 5
            },
            XAVI voted {
                POLONIA += 10
            },
            CHRISTOPH voted {
                POLONIA += 10
            },
            KATIE voted {
                WRESTLERS += 10
            },
            DOM voted {
                THE_OLD_SPRING += 10
            },
            ARTEM voted {
                POLONIA += 10
            },
            JON voted { },
            CHARLES voted { },
            DAVID_B voted { }
    )

    val groups = GroupSplitter(10).splitGroups(votes.map { it.person })

    val voteMap = votes
            .map { it.person to it.vote }
            .toMap()
    val groupVotes = consolidateGroupVotes(groups, voteMap)

    runVote(groupVotes)

}

private fun runVote(groupVotes: Map<Group, Map<Place, Int>>) {
    val voters = groupVotes.mapValues { WeightedRandomVoter() }

    var complete = false
    while (!complete) {
        complete = true
        val winners = mutableSetOf<Place>()
        for ((people, voter) in voters) {
            val winner = voter.runVote(StandardVoteInput(groupVotes.getOrDefault(people, mapOf<Place, Int>())))
            if (winners.contains(winner)) {
                complete = false
            }
            winners.add(winner)
        }
    }

    voters.forEach({ (group, voter) ->
        println(voter.getOutput())
        println("$group are going to ${voter.choice}")
    })
}

private fun consolidateGroupVotes(groups: List<Group>, votes: Map<People, Map<Place, Int>>): Map<Group, Map<Place, Int>> {
    return groups.map { group ->
        val totals = mutableMapOf<Place, Int>()
        group.forEach {
            votes[it]!!.forEach { (place, value) ->
                totals.merge(place, value, Int::plus)
            }
        }
        group to totals
    }.toMap()
}