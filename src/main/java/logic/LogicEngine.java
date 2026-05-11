package logic;

import java.util.List;

import model.DataTemplate;

public class LogicEngine<T extends DataTemplate> {

    public void sortById(List<T> list) {
        list.sort((a, b) -> a.getId() - b.getId());
    }

    public void sortByNama(List<T> list) {
        list.sort((a, b) -> a.getNama().compareToIgnoreCase(b.getNama()));
    }

    public T smartSearch(List<T> list, int id) {
        for (T item : list) {
            if (item.getId() == id) return item;
        }
        return null;
    }

    public T smartSearch(List<T> list, String keyword) {
        boolean isAngka = keyword.matches("\\d+");
        for (T item : list) {
            if (isAngka) {
                if (item.getId() == Integer.parseInt(keyword)) return item;
            } else {
                if (item.getNama().equalsIgnoreCase(keyword)) return item;
            }
        }
        return null;
    }
}