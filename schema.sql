--
-- PostgreSQL database dump
--

-- Dumped from database version 10.13
-- Dumped by pg_dump version 12.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

--
-- Name: data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.data (
    time_tag timestamp without time zone NOT NULL,
    density numeric,
    speed numeric,
    bz_gsm numeric
);


ALTER TABLE public.data OWNER TO postgres;

--
-- Name: magnetometer_han; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.magnetometer_han (
    time_tag timestamp without time zone NOT NULL,
    mag_x numeric,
    mag_y numeric,
    mag_z numeric
);


ALTER TABLE public.magnetometer_han OWNER TO postgres;

--
-- Name: magnetometer_kev; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.magnetometer_kev (
    time_tag timestamp without time zone NOT NULL,
    mag_x numeric,
    mag_y numeric,
    mag_z numeric
);


ALTER TABLE public.magnetometer_kev OWNER TO postgres;

--
-- Name: magnetometer_nur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.magnetometer_nur (
    time_tag timestamp without time zone NOT NULL,
    mag_x numeric,
    mag_y numeric,
    mag_z numeric
);


ALTER TABLE public.magnetometer_nur OWNER TO postgres;

--
-- Name: magnetometer_ouj; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.magnetometer_ouj (
    time_tag timestamp without time zone NOT NULL,
    mag_x numeric,
    mag_y numeric,
    mag_z numeric
);


ALTER TABLE public.magnetometer_ouj OWNER TO postgres;

--
-- Name: sessions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sessions (
    chat_id bigint,
    is_started boolean,
    is_timezone boolean,
    timezone text,
    is_archive boolean,
    archive text,
    is_notif boolean
);


ALTER TABLE public.sessions OWNER TO postgres;

--
-- Name: data data_time_tag_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.data
    ADD CONSTRAINT data_time_tag_key UNIQUE (time_tag);


--
-- Name: magnetometer_han data_time_tag_key_han; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.magnetometer_han
    ADD CONSTRAINT data_time_tag_key_han UNIQUE (time_tag);


--
-- Name: magnetometer_kev data_time_tag_key_kev; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.magnetometer_kev
    ADD CONSTRAINT data_time_tag_key_kev UNIQUE (time_tag);


--
-- Name: magnetometer_nur data_time_tag_key_nur; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.magnetometer_nur
    ADD CONSTRAINT data_time_tag_key_nur UNIQUE (time_tag);


--
-- Name: magnetometer_ouj data_time_tag_key_ouj; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.magnetometer_ouj
    ADD CONSTRAINT data_time_tag_key_ouj UNIQUE (time_tag);


--
-- Name: sessions unique_chat_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sessions
    ADD CONSTRAINT unique_chat_id UNIQUE (chat_id);


--
-- PostgreSQL database dump complete
--

