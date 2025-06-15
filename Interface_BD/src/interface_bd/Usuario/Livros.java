package interface_bd.Usuario;

import interface_bd.Api.ApiService;
import interface_bd.Api.ApiService.BookData;
import interface_bd.Api.ApiService.GenericResponse;
import interface_bd.Login.ERRO;
import interface_bd.Login.Login;
import java.awt.Dimension;
import javax.swing.table.TableColumn;
import java.awt.event.MouseEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.MouseAdapter;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.List;
import java.util.Vector; // Para DefaultTableModel

/**
 *
 * @author Usuario
 */
public class Livros extends javax.swing.JFrame {

    private Dimension minimumSize;
    private Dimension preferredSize;
    private JFrame parentFrame;

    // Coluna oculta para armazenar o ID do livro
    private static final int ID_COLUMN_INDEX = 7; // Ajuste conforme a quantidade de colunas visíveis + 1

    /**
     * Creates new form Livros
     */
    public Livros(JFrame parent) {
        this.parentFrame = parent;
        initComponents();
        this.setTitle("Meus Livros");
        this.setLocationRelativeTo(parent);

        // Adiciona um WindowListener para lidar com o fechamento da janela
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (parentFrame != null) {
                    parentFrame.setEnabled(true);
                    parentFrame.toFront();
                }
            }
        });

        // Configura a tabela (modelo, redimensionamento, renderizador de tooltip)
        setupTable();
        
        // Carrega os livros do usuário ao abrir a tela
        loadUserBooks();
    }

    /**
     * Configura o modelo da tabela e o renderizador para tooltips.
     */
    private void setupTable() {
        // Define o modelo da tabela manualmente para controlar as colunas e tipos
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, // Sem dados iniciais
            new String[]{"Titulo", "Autor", "Genero", "Status", "Nota", "Comentario", "Sinopse", "ID"} // "ID" será a coluna oculta
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class // ID como String
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            // Faz todas as células editáveis (vamos controlar a edição no mouseClicked)
            @Override
            public boolean isCellEditable(int row, int column) {
                // A coluna ID não deve ser editável
                return column != ID_COLUMN_INDEX; 
            }
        };
        TB_Livros.setModel(model);

        redimensionarColunasTabela(); // Chama o método para redimensionar

        // Esconde a coluna de ID
        TableColumn idColumn = TB_Livros.getColumnModel().getColumn(ID_COLUMN_INDEX);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setWidth(0);
        idColumn.setPreferredWidth(0);
        TB_Livros.getTableHeader().getColumnModel().getColumn(ID_COLUMN_INDEX).setMinWidth(0);
        TB_Livros.getTableHeader().getColumnModel().getColumn(ID_COLUMN_INDEX).setMaxWidth(0);
        TB_Livros.getTableHeader().getColumnModel().getColumn(ID_COLUMN_INDEX).setWidth(0);
    }
    
    /**
     * Carrega os livros do usuário a partir da API e popula a tabela.
     */
    private void loadUserBooks() {
        DefaultTableModel model = (DefaultTableModel) TB_Livros.getModel();
        model.setRowCount(0); // Limpa a tabela antes de carregar novos dados

        ApiService apiService = ApiService.getInstance();

        new Thread(() -> {
            try {
                List<BookData> books = apiService.getUserBooks();

                java.awt.EventQueue.invokeLater(() -> {
                    if (books != null && !books.isEmpty()) {
                        for (BookData book : books) {
                            model.addRow(new Object[]{
                                book.getTitle(),
                                book.getAuthor(),
                                book.getGenre(),
                                book.getStatus(),
                                book.getRating(),
                                book.getComment(),
                                book.getSynopsis(),
                                book.get_id() // ID na coluna oculta
                            });
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Nenhum livro encontrado.", "Meus Livros", JOptionPane.INFORMATION_MESSAGE);
                    }
                });

            } catch (IllegalStateException e) {
                java.awt.EventQueue.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
                    this.dispose();
                    parentFrame.dispose(); // Fecha a tela pai (Usuario) também
                    Login.getInstance().setVisible(true); // Volta para o login
                });
            } catch (IOException e) {
                e.printStackTrace();
                java.awt.EventQueue.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Erro de comunicação com o servidor ao carregar livros: " + e.getMessage() + ". Verifique o backend.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                    new ERRO(this, true).setVisible(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                java.awt.EventQueue.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Um erro inesperado ocorreu ao carregar livros: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    new ERRO(this, true).setVisible(true);
                });
            }
        }).start();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // </editor-fold>//GEN-END:initComponents
// </editor-fold>//GEN-END:initComponents
// </editor-fold>//GEN-END:initComponents
// </editor-fold>//GEN-END:initComponents





    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void redimensionarColunasTabela() {
        TB_Livros.setRowHeight(25); // Altura desejada para cada linha em pixels

        int[] largurasColunas = {
            150, // Titulo
            120, // Autor
            100, // Genero
            80, // Status
            60, // Nota
            250, // Comentario
            350 // Sinopse
            // A coluna de ID não precisa de largura, pois está oculta
        };

        for (int i = 0; i < largurasColunas.length; i++) { // Percorre apenas as colunas visíveis
            TableColumn coluna = TB_Livros.getColumnModel().getColumn(i);
            coluna.setPreferredWidth(largurasColunas[i]);

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                        javax.swing.JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    java.awt.Component c = super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
                    if (value != null) {
                        setToolTipText(value.toString());
                    } else {
                        setToolTipText(null);
                    }
                    return c;
                }
            };
            coluna.setCellRenderer(renderer);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PaLivros = new javax.swing.JPanel();
        TR_Livros = new javax.swing.JToolBar();
        LA_Livros = new java.awt.Label();
        B_Sair = new java.awt.Button();
        SP_Livros = new javax.swing.JScrollPane();
        TB_Livros = new javax.swing.JTable();
        B_excluir = new java.awt.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Livros");
        setBackground(new java.awt.Color(220, 220, 220));
        setMinimumSize(this.preferredSize);
        setResizable(false);

        PaLivros.setBackground(new java.awt.Color(220, 220, 220));
        PaLivros.setForeground(new java.awt.Color(0, 0, 0));
        PaLivros.setMaximumSize(this.minimumSize);
        PaLivros.setMinimumSize(this.preferredSize);
        PaLivros.setPreferredSize(new java.awt.Dimension(582, 333));

        TR_Livros.setBackground(new java.awt.Color(210, 210, 210));
        TR_Livros.setRollover(true);

        LA_Livros.setAlignment(java.awt.Label.CENTER);
        LA_Livros.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LA_Livros.setForeground(new java.awt.Color(60, 60, 60));
        LA_Livros.setMinimumSize(new java.awt.Dimension(100, 100));
        LA_Livros.setName("Livros"); // NOI18N
        LA_Livros.setText("Livros");
        TR_Livros.add(LA_Livros);

        B_Sair.setActionCommand("Sair");
        B_Sair.setLabel("Sair");
        B_Sair.setName("Sair"); // NOI18N
        B_Sair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_SairActionPerformed(evt);
            }
        });

        SP_Livros.setMaximumSize(this.minimumSize);
        SP_Livros.setMinimumSize(this.preferredSize);
        SP_Livros.setPreferredSize(new java.awt.Dimension(582, 285));
        SP_Livros.setViewportView(TB_Livros);

        TB_Livros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Titulo", "Autor", "Genero", "Status", "Nota", "Comentario", "Sinopse"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        TB_Livros.setToolTipText("\"\"");
        TB_Livros.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        TB_Livros.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        TB_Livros.setMinimumSize(new java.awt.Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE));
        TB_Livros.setPreferredSize(new java.awt.Dimension(582, 285));
        TB_Livros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TB_LivrosMouseClicked(evt);
            }
        });
        SP_Livros.setViewportView(TB_Livros);

        B_excluir.setActionCommand("Sair");
        B_excluir.setLabel("Excluir");
        B_excluir.setName("Excluir"); // NOI18N
        B_excluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_excluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PaLivrosLayout = new javax.swing.GroupLayout(PaLivros);
        PaLivros.setLayout(PaLivrosLayout);
        PaLivrosLayout.setHorizontalGroup(
            PaLivrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TR_Livros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(PaLivrosLayout.createSequentialGroup()
                .addComponent(B_Sair, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(B_excluir, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(SP_Livros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PaLivrosLayout.setVerticalGroup(
            PaLivrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PaLivrosLayout.createSequentialGroup()
                .addComponent(TR_Livros, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(PaLivrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(B_Sair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PaLivrosLayout.createSequentialGroup()
                        .addGap(0, 0, 0)
                        .addComponent(B_excluir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(SP_Livros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PaLivros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PaLivros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void B_SairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_SairActionPerformed
  
    }//GEN-LAST:event_B_SairActionPerformed

    private void B_excluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_excluirActionPerformed
    
   

    }//GEN-LAST:event_B_excluirActionPerformed

    private void TB_LivrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TB_LivrosMouseClicked
      
    }//GEN-LAST:event_TB_LivrosMouseClicked

    /**
     * @param args the command line arguments
     */
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button B_Sair;
    private java.awt.Button B_excluir;
    private java.awt.Label LA_Livros;
    private javax.swing.JPanel PaLivros;
    private javax.swing.JScrollPane SP_Livros;
    private javax.swing.JTable TB_Livros;
    private javax.swing.JToolBar TR_Livros;
    // End of variables declaration//GEN-END:variables

    
}
         
