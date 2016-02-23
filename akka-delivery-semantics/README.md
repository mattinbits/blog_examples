#Akka delivery guarantees

See http://www.mjlivesey.co.uk/2016/02/19/akka-delivery-guarantees.html

Run with

    sbt run <type> <sender_mailbox> <receiver_mailbox>
    
Where type is one of `basic`, `ack`, `idempotent` and the
mailboxes are either `akka.actor.default-mailbox` or `unreliable`.