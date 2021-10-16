FROM postgres:13-bullseye

# Postgresql with wal2json plugin
ENV POSTGRES_PASSWORD=secret
ENV POSTGRES_USER=postgres

COPY postgresql.conf      /tmp/postgresql.conf
CMD ["postgres", "-c", "config_file=/tmp/postgresql.conf"]
RUN apt update -y
RUN apt install postgresql-server-dev-13 -y
RUN apt install postgresql-13-wal2json -y