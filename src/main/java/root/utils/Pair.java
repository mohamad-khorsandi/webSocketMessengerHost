package root.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<K, V> {
    public Pair(K key, V val) {
        this.key = key;
        this.val = val;
    }

    K key;
    V val;
}
