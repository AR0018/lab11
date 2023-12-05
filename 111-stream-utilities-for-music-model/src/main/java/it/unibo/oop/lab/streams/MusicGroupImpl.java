package it.unibo.oop.lab.streams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
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
        /*  LESS EFFICIENT
        return Optional.of(this.songs.stream()
            .map((song) -> Map.entry(song, song.getDuration()))
            .max(Map.Entry.comparingByValue(Double::compareTo))
            .get().getKey().getSongName());*/
        return Optional.of(this.songs.stream()
            //Creates a Map that associates each Song to its duration
            .collect(Collectors.toMap(Function.identity(), Song::getDuration))
            .entrySet().stream()
            .max(Entry.comparingByValue(Double::compareTo))
            .get().getKey().getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return songs.stream()
            .filter((song) -> song.getAlbumName().isPresent())
            //Creates a map that associates each album name to its total duration
            .collect(Collectors.groupingBy(
                (song) -> song.getAlbumName(),
                Collectors.mapping(Song::getDuration, Collectors.reducing(Double::sum))))
            .entrySet().stream()
            .max(Entry.comparingByValue((dur1, dur2) -> dur1.get().compareTo(dur2.get())))
            .get().getKey();
    }

    /*
     * Auxiliary method that returns a Stream containing all the songs in the given album
     */
    private Stream<Song> filterByAlbum(final String albumName) {
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
