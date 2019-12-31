# About

This is a Java Email Server. It is using Apache James and configures it. Noteworthy points:
- It uses Apache James
- It uses MariaDB
- It has a *Manager* that checks in real-time if its config file changed and it will apply the changes. It manages:
	- The domains
	- The accounts with their passwords (in clear text or SHA-512 hash)
	- The redirections
- Instead of Java keystores, it supports PEM files (will generate self-signed certs if nothing is provided)

The current configuration for James:
- When a local account is sending an email, the authentified username (the email) and the "from" field of the email must be the same (to prevent impersonation)
- Emails sent for a specific domain can be sent by the server itself or via an external Gateway (like Amazon SES, SendGrid, ...)
- Redirection emails
	- Can have a catchall email per domain that will get all the emails that do not belong to a local account or that do not have a specific redirection
	- Can redirect to multiple recipients

# Development

## Always start a MariaDB database

```
./test-mariadb-start.sh
```

## Local in Eclipse

Run *Email - Application.launch*

You can then start without any parameter. That will automatically:
- use the default database
- create *_workdir/email-manager-config.json*
- Create the account account@localhost.foilen-lab.com with password "qwerty"
- Create a catch-all redirection *@localhost.foilen-lab.com that goes to account@localhost.foilen-lab.com

## Local in Docker

```
./test-server-local.sh
```

# Real run

## Configuration

```
TMPDIR=$(mktemp -d)
cd $TMPDIR
cat > james-config.json << _EOF
{
  "database" : {
    "hostname" : "127.0.0.1",
    "database" : "db_database_email",
    "port" : 3306,
    "username" : "email",
    "password" : "ABC"
  },
  "postmasterEmail" : "postmaster@example.com",
  "enableDebugDumpMessagesDetails" : false,
  "disableRelayDeniedNotifyPostmaster" : false,
  "disableBounceNotifyPostmaster" : false,
  "disableBounceNotifySender" : false,
  "domainAndRelais" : []
}
_EOF
cat > manager-config.json << _EOF
{
  "domains" : [ "example.com" ],
  "accounts" : [ {
    "email" : "account1@example.com",
    "passwordSha512" : "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f"
  }, {
    "email" : "account2@example.com",
    "password" : "efg"
  } ],
  "redirections" : [ {
    "email" : "redir1@example.com",
    "redirectTos" : [ "account1@example.com" ]
  }, {
    "email" : "*@example.com",
    "redirectTos" : [ "account2@example.com" ]
  } ]
}
_EOF
```

## Logs

Logs are placed in "workdir/logs/"
