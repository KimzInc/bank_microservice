-- ============================================================================
-- auth_db — credentials/identity store for auth-service
-- Run: sudo -u postgres psql < /path/to/create_auth_db.sql
-- ============================================================================

-- Uncomment if you need to re-run this script against an existing database
-- (careful: this destroys all data in it)
-- DROP DATABASE IF EXISTS auth_db;

-- Create the database
CREATE DATABASE auth_db
    WITH
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- Add a comment describing what this database is for
COMMENT ON DATABASE auth_db IS 'auth-service: user credentials, password hashes, and roles used to issue and validate JWTs.';

-- Grant privileges (only needed if you connect as a role other than postgres;
-- config-repo/auth-service.yml currently connects as the postgres superuser)
   GRANT ALL PRIVILEGES ON DATABASE auth_db TO kimzinc;

-- IMPORTANT (PostgreSQL 15+): a database-level GRANT above does NOT include
-- rights inside the "public" schema - since PG15, only the schema owner (and
-- superusers) can CREATE there by default. Without this, Hibernate's
-- ddl-auto: update fails with "permission denied for schema public" the
-- moment it tries to create your first table. \c switches into the new
-- database so the grant below applies to ITS public schema, not postgres's.
   \c auth_db
   GRANT ALL ON SCHEMA public TO kimzinc;

\echo 'Database auth_db created successfully!'
