package it.unibo.oop.lab.streams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
            .map(Song::getSongName)
            .sorted(String::compareTo);
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream()
            .filter((entry) -> entry.getValue().equals(year))
            .map(Map.Entry::getKey);
    }

    @Override
    public int countSongs(final String albumName) {        
        return (int) filterByAlbum(albumName)
            .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
            .filter((song) -> song.getAlbumName().isEmpty())
            .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return filterByAlbum(albumName)
            .mapToDouble(Song::getDuration)
            .average();
    }

    @Override
    public Optional<String> longestSong() {
        return Optional.of(this.songs.stream()
            .map((song) -> Map.entry(song, song.getDuration()))
            .max(Map.Entry.comparingByValue(Double::compareTo))
            .get().getKey().getSongName());
        /* TODO: utilizzare un Collector per ritornare una mappa 
        * e poi creare uno Stream su quella mappa (EntrySet)
        */
    }

    @Override
    public Optional<String> longestAlbum() {
        /*TODO implement */
        return null;
    }

    /*
     * Auxiliary method that returns a Stream containing all the songs in the given album
     */
    private Stream<Song> filterByAlbum(String albumName) {
        return this.songs.stream()
            .filter((song) -> !song.getAlbumName().isEmpty())
            .filter((song) -> song.getAlbumName().get().equals(albumName));
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
